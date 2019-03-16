package com.github.amap.track;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.amap.track.util.JsonUtil;

/**
 * 猎鹰服务中的终端，终端可以新建轨迹
 * 
 * @author wy
 *
 */
public class Terminal extends BaseOpr implements Serializable {

	private static final long serialVersionUID = -7427564709023099668L;
	public static final String DEFAULT_TRID_KEY = "defaultTrid";

	protected Service service;
	// 高德服务提交了否
	private volatile boolean commited = false;

	private Integer tid;
	private String name;
	private String desc;
	private final Map<String, String> props = new HashMap<>();

	private long createtime;
	private long locatetime;

	void add(String name, String desc, Map<String, String> props) {
		Map<String, Object> queryMap = service.getBaseParams();
		queryMap.put("name", name);
		queryMap.put("desc", desc);
		if (props != null)
			queryMap.put("props", JsonUtil.toJson(props));

		String result = postForm(Const.TERMINAL_ADD_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			setTid((Integer) ((Map) resultMap.get("data")).get("tid"));
			setName(name);
			setDesc(desc);
			setProps(props);
			setCommited(true);
		} else {
			throw new AMapTrackException("添加终端失败: " + result);
		}

	}

	/**
	 * 对应高德API https://lbs.amap.com/api/track/lieying-kaifa/api/track#t4
	 * 
	 * 删除该终端
	 */
	public void delete() {
		Map<String, Object> queryMap = service.getBaseParams();
		queryMap.put("tid", tid);

		String result = postForm(Const.TERMINAL_DELETE_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			tid = null;
			setCommited(false);
		} else {
			throw new AMapTrackException("删除终端失败: " + result);
		}
	}

	/**
	 * 修改终端信息
	 * 
	 * @param name  名称
	 * @param desc  描述
	 * @param props 属性，必须先要添加属性才能使用
	 */
	public void update(String name, String desc, Map<String, String> props) {
		Map<String, Object> queryMap = service.getBaseParams();
		queryMap.put("tid", tid);
		if (name != null)
			queryMap.put("name", name);
		if (desc != null)
			queryMap.put("desc", desc);
		if (props == null || props.size() == 0) {
		} else {
			queryMap.put("props", JsonUtil.toJson(props));
		}

		String result = postForm(Const.TERMINAL_UPDATE_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			setName(name);
			setDesc(desc);
			setProps(props);
			setCommited(true);
		} else {
			throw new AMapTrackException("修改终端失败: " + result);
		}
	}

	Terminal list(String name) {
		List<Terminal> list = list(null, name, null);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	Terminal list(Integer tid) {
		List<Terminal> lists = list(tid, null, null);
		if (lists != null && lists.size() > 0) {
			return lists.get(0);
		} else {
			return null;
		}
	}

	List<Terminal> list(Integer tid, String name, Integer page) {
		Map<String, Object> queryMap = service.getBaseParams();
		if (tid != null)
			queryMap.put("tid", tid);
		if (name != null)
			queryMap.put("name", name);
		if (page != null)
			queryMap.put("page", page);

		String result = get(Const.TERMINAL_LIST_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			if (resultMap.get("data") == null) {
				return new ArrayList<>(0);
			}

			Integer count = (Integer) ((Map) resultMap.get("data")).get("count");
			List list = (List) ((Map) resultMap.get("data")).get("results");
			List<Terminal> results = new ArrayList<>(count);
			if (list == null)
				return results;
			for (Object obj : list) {
				Map sMap = (Map) obj;
				Terminal ss = new Terminal();
				ss.setService(this.getService());
				ss.setName((String) sMap.get("name"));
				ss.setTid((Integer) sMap.get("tid"));
				ss.setDesc((String) sMap.get("desc"));
				ss.setCreatetime(Long.parseLong(sMap.get("createtime").toString()));
				ss.setLocatetime(Long.parseLong(sMap.get("locatetime").toString()));
				fillProps(sMap, ss);
				ss.setCommited(true);
				results.add(ss);
			}
			return results;
		} else {
			throw new AMapTrackException("查询终端失败: " + result);
		}
	}

	private void fillProps(Map sMap, Terminal ss) {
		if (sMap.get("props") == null) {
			ss.setProps(new HashMap<>());
		} else {
			Map<String, String> props = (Map<String, String>) sMap.get("props");
			ss.setProps(props);
		}
	}

	/**
	 * 为此终端新建一个轨迹
	 * 
	 * @return
	 */
	public Trace createTrace() {
		Trace trace = new Trace();
		trace.setTerminal(this);
		trace.add();
		return trace;
	}

	/**
	 * 创建默认轨迹，就是把新建的轨迹trid存入到Terminal的属性中
	 * 
	 * @return
	 */
	public Trace createDefaultTrace() {
		String string = props.get("defaultTrid");
		if (string != null && !string.isEmpty()) {
			try {
				Trace defaultTrace2 = defaultTrace();
				return defaultTrace2;
			} catch (AMapTrackException e) {

			}
		}

		Trace defaultTrace = createTrace();
		props.put("defaultTrid", defaultTrace.getTrid() + "");
		this.update(name, desc, props);
		defaultTrace.setDefaultTrace(true);
		return defaultTrace;
	}

	/**
	 * 获取默认轨迹
	 * 
	 * @return
	 */
	public Trace defaultTrace() {
		Trace trace = new Trace();
		trace.setTerminal(this);
		String string = props!=null?props.get("defaultTrid"):null;
		if (string == null || string.trim().length() == 0) {
			throw new AMapTrackException("没有找到终端的默认轨迹");
		}
		int trid = Integer.parseInt(string);
		trace.setTrid(trid);
		trace.setDefaultTrace(true);
		return trace;
	}

	protected Map<String, Object> getBaseParams() {
		Map<String, Object> queryMap = service.getBaseParams();
		queryMap.put("tid", tid);

		return queryMap;
	}

	public Service getService() {
		return service;
	}

	void setService(Service service) {
		this.service = service;
	}

	public boolean isCommited() {
		return commited;
	}

	void setCommited(boolean commited) {
		this.commited = commited;
	}

	public String getName() {
		return name;
	}

	Terminal setName(String name) {
		this.name = name;
		return this;
	}

	public String getDesc() {
		return desc;
	}

	void setDesc(String desc) {
		this.desc = desc;
	}

	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		synchronized (this) {
			this.props.clear();
			if(props!=null) {
				props.forEach((k, v)->{
					this.props.put(k, v);
				});
			}
		}
	}

	public Integer getTid() {
		return tid;
	}

	void setTid(Integer tid) {
		this.tid = tid;
	}

	public long getCreatetime() {
		return createtime;
	}

	void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public long getLocatetime() {
		return locatetime;
	}

	void setLocatetime(long locatetime) {
		this.locatetime = locatetime;
	}

	@Override
	public String toString() {
		return "Terminal [tid=" + tid + ", name=" + name + ", desc=" + desc + ", props=" + props + ", createtime="
				+ createtime + ", locatetime=" + locatetime + "]";
	}

}
