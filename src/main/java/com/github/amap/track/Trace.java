package com.github.amap.track;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.amap.track.util.JsonUtil;

/**
 * 表示猎鹰服务中一个轨迹，可以查询和添加点
 * 
 * @author wy
 *
 */
public class Trace extends BaseOpr implements Serializable {

	private static final long serialVersionUID = 2624978752323824201L;

	protected Terminal terminal;
	// 高德服务提交了否
	private volatile boolean commited = false;

	private Integer trid;
	private volatile boolean defaultTrace = false;

	void add() {
		Map<String, Object> queryMap = terminal.getBaseParams();

		String result = postForm(Const.TRACE_ADD_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			setTrid((Integer) ((Map) resultMap.get("data")).get("trid"));
			setCommited(true);
		} else {
			throw new AMapTrackException("添加轨迹失败: " + result);
		}

	}

	/**
	 * 对应高德API https://lbs.amap.com/api/track/lieying-kaifa/api/track#t4 删除该轨迹
	 */
	public void delete() {
		Map<String, Object> queryMap = terminal.getBaseParams();
		queryMap.put("trid", trid);

		String result = postForm(Const.TRACE_DELETE_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			trid = null;
			setCommited(false);
			// 如果是默认轨迹则要删除
			if (defaultTrace) {
				Map<String, String> props = terminal.getProps();
				props.put("defaultTrid", "");
				props.put("lastPointTime", "");
				terminal.update(terminal.getName(), terminal.getDesc(), props);
				defaultTrace = false;
			}
		} else {
			throw new AMapTrackException("删除轨迹失败: " + result);
		}
	}

	/**
	 * 给轨迹添加一个点
	 * 
	 * @param p
	 */
	public void uploadPoint(Point p) {
		List<Point> ps = new ArrayList<>(1);
		ps.add(p);
		uploadPoints(ps);
	}

	/**
	 * 给轨迹添加一组点
	 * 
	 * @param p
	 */
	public void uploadPoints(List<Point> ps) {
		Map<String, Object> queryMap = getBaseParams();
		queryMap.put("points", toJSON(ps));
		String result = postForm(Const.POINT_UPLOAD_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			setCommited(true);
		} else {
			throw new AMapTrackException("添加轨迹点失败: " + result);
		}
	}

	private String toJSON(List<Point> ps) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (ps != null && ps.size() > 0) {
			for (Point point : ps) {
				sb.append(point.toJSON()).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 对应高德API https://lbs.amap.com/api/track/lieying-kaifa/api/grasproad 返回
	 * 纠偏或者补充后的点
	 * 
	 * 
	 * @param starttime 可为空
	 * @param endtime   可为空
	 * @param recoup    可为空 表示是否填充距离大于gap的点
	 * @param gap       定义两点之间需要补点的最小距离 50<= gap <=10000 单位m
	 * @param page      查询第几页
	 * @param pagesize  每页数据条数
	 * @return Points
	 */
	public Points trsearch(Long starttime, Long endtime, Boolean recoup, Integer gap, Integer page, Integer pagesize) {
		Map<String, Object> queryMap = getBaseParams();
		if (starttime != null)
			queryMap.put("starttime", starttime);
		if (endtime != null)
			queryMap.put("endtime", endtime);
		if (page != null)
			queryMap.put("page", page);
		if (pagesize != null)
			queryMap.put("pagesize", pagesize);
		if (gap != null)
			queryMap.put("gap", gap);
		if (recoup != null && recoup)
			queryMap.put("recoup", 1);

		String result = get(Const.TERMINAL_TRSEARCH_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);
		if (resultMap == null || !"OK".equals((String) resultMap.get("errmsg"))) {
			throw new AMapTrackException("获取轨迹失败: " + result);
		}

		if (resultMap.get("data") == null) {
			return null;
		}

		Integer count = (Integer) ((Map) resultMap.get("data")).get("counts");
		List tracks = (List) ((Map) resultMap.get("data")).get("tracks");

		if (tracks == null || tracks.size() == 0)
			return null;
		Map sMap = (Map) tracks.get(0);

		Points ss = new Points();
		ss.setTrace(this);
		ss.setDistance((Integer) sMap.get("distance"));

		if (sMap.get("time") instanceof Integer) {
			int a = (Integer) sMap.get("time");
			ss.setTime(Long.parseLong(a+""));
		}
		if (sMap.get("time") instanceof Long) {
			ss.setTime((Long) sMap.get("time"));
		}
		List<Point> pos = extractPoints(sMap);
		ss.setPoints(pos);
		return ss;
	}

	private List<Point> extractPoints(Map sMap) {
		List<Point> pos = new ArrayList<>();
		List points = (List) sMap.get("points");
		for (Object object : points) {
			Map mp = (Map) object;
			if (mp == null)
				continue;
			Point e = new Point();
			e.setLocatetime((Long) Optional.ofNullable(mp.get("locatetime")).orElse(0L));
			e.setXY((String) mp.get("location"));
			Object object2 = mp.get("props");

			try {
				e.setProps((Map<String, String>) JsonUtil.toObject(mp.get("props").toString(), Map.class));
			} catch (Exception e1) {
				e.setProps(null);
			}
			pos.add(e);
		}
		return pos;
	}

	public Points createPointOpr() {
		Points p = new Points();
		p.setTrace(this);
		return p;
	}

	public boolean isCommited() {
		return commited;
	}

	void setCommited(boolean commited) {
		this.commited = commited;
	}

	public Integer getTrid() {
		return trid;
	}

	void setTrid(Integer trid) {
		this.trid = trid;
	}

	public Terminal getTerminal() {
		return terminal;
	}

	void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public Map<String, Object> getBaseParams() {
		Map<String, Object> baseParams = terminal.getBaseParams();
		baseParams.put("trid", trid);
		return baseParams;
	}

	public boolean isDefaultTrace() {
		return defaultTrace;
	}

	void setDefaultTrace(boolean defaultTrace) {
		this.defaultTrace = defaultTrace;
	}

	@Override
	public String toString() {
		return "Trace [terminal=" + terminal + ", trid=" + trid + ", defaultTrace=" + defaultTrace + "]";
	}
}
