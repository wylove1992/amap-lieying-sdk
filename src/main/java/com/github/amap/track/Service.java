package com.github.amap.track;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.amap.track.util.JsonUtil;

/**
 * 表示猎鹰服务中Service，使用此类创建Terminal等
 * 
 * @author wy
 *
 */
public class Service extends BaseOpr implements Serializable {
	private static final long serialVersionUID = -1139965967915347033L;
	private AMapTrackClient client;

	// 高德服务提交了否
	private volatile boolean commited = false;

	private Integer sid;
	private String name;
	private String desc;

	void add() {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		queryMap.put("name", name);
		queryMap.put("desc", desc);

		String result = postForm(Const.SERVICE_ADD_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			sid = (Integer) ((Map) resultMap.get("data")).get("sid");
			setCommited(true);
		} else {
			throw new AMapTrackException("添加服务失败: " + result);
		}

	}

	/**
	 * 删除这个service
	 */
	public void delete() {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		queryMap.put("sid", sid);

		String result = postForm(Const.SERVICE_DELETE_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			sid = null;
			setCommited(false);
		} else {
			throw new AMapTrackException("删除服务失败: " + result);
		}
	}

	/**
	 * 修改service的信息
	 * 
	 * @param name
	 * @param desc
	 */
	public void update(String name, String desc) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		queryMap.put("sid", sid);
		queryMap.put("name", name);
		queryMap.put("desc", desc);

		String result = postForm(Const.SERVICE_UPDATE_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			setName(name);
			setDesc(desc);
			setCommited(true);
		} else {
			throw new AMapTrackException("修改服务失败: " + result);
		}
	}

	List<Service> list() {
		List<Service> results = new ArrayList<>();
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		String result = get(Const.SERVICE_LIST_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
			List list = (List) ((Map) resultMap.get("data")).get("results");
			if (list == null)
				return results;
			for (Object obj : list) {
				Map sMap = (Map) obj;
				Service ss = new Service();
				ss.setClient(client);
				ss.setSid((Integer) sMap.get("sid"));
				ss.setName((String) sMap.get("name"));
				ss.setDesc((String) sMap.get("desc"));
				ss.setCommited(true);
				results.add(ss);
			}
			return results;
		} else {
			throw new AMapTrackException("查询服务失败: " + result);
		}
	}

	/**
	 * 在此Service下新建一个Terminal
	 * 
	 * @param name  终端名称
	 * @param desc  描述
	 * @param props 属性，需要给终端添加属性才能在这里使用
	 * @return Terminal
	 */
	public Terminal createTerminal(String name, String desc, Map<String, String> props) {
		Terminal t = new Terminal();
		t.setService(this);
		t.add(name, desc, props);
		return t;
	}

	/**
	 * 找到当前Service下的制定tid的终端
	 * 
	 * @param tid
	 * @return Terminal
	 */
	public Terminal listTerminal(Integer tid) {
		Terminal t = new Terminal();
		t.setService(this);
		return t.list(tid);
	}

	/**
	 * 找到当前Service下制定名称的Terminal
	 * 
	 * @param name
	 * @return Terminal
	 */
	public Terminal listTerminal(String name) {
		Terminal t = new Terminal();
		t.setService(this);
		return t.list(name);
	}

	/**
	 * 列出当前Service下的Terminal 按照制定条件过滤
	 * 
	 * @param tid  可为空
	 * @param name 可为空
	 * @param page 可为空
	 * @return List<Terminal>
	 */
	public List<Terminal> listTerminal(Integer tid, String name, Integer page) {
		Terminal t = new Terminal();
		t.setService(this);
		return t.list(tid, name, page);
	}

	/**
	 * 为当前服务下的终端添加属性，必须现在此处添加属性，终端的props字段里面才可以加入指定的值
	 * 
	 * @param column
	 * @param type
	 * @param canList
	 */
	public void addPropsForTerminal(String column, String type, boolean canList) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		queryMap.put("sid", sid);
		queryMap.put("column", column);
		queryMap.put("type", type);
		queryMap.put("list", canList ? "y" : "n");

		String result = postForm(Const.COLUMN_ADD_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
		} else {
//			throw new AMapTrackException("添加终端属性失败: " + result);
		}
	}

	/**
	 * 删除该服务下的终端属性
	 * 
	 * @param column
	 */
	public void delPropsForTerminal(String column) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		queryMap.put("sid", sid);
		queryMap.put("column", column);

		String result = postForm(Const.COLUMN_DELETE_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
		} else {
			throw new AMapTrackException("删除终端属性失败: " + result);
		}
	}
	
	/**
	 * 为当前服务下的终端添加属性，必须现在此处添加属性，终端的props字段里面才可以加入指定的值
	 * 
	 * @param column
	 * @param type
	 * @param canList
	 */
	public void addPropsForTrace(String column, String type) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		queryMap.put("sid", sid);
		queryMap.put("column", column);
		queryMap.put("type", type);

		String result = postForm(Const.TRACE_COLUMN_ADD_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
		} else {
//			throw new AMapTrackException("添加终端属性失败: " + result);
		}
	}

	/**
	 * 删除该服务下的终端属性
	 * 
	 * @param column
	 */
	public void delPropsForTrace(String column) {
		Map<String, Object> queryMap = new HashMap<>();
		queryMap.put("key", client.key);
		queryMap.put("sid", sid);
		queryMap.put("column", column);

		String result = postForm(Const.TRACE_COLUMN_DELETE_URL, queryMap);
		Map resultMap = JsonUtil.toObject(result, Map.class);

		if (resultMap != null && "OK".equals((String) resultMap.get("errmsg"))) {
		} else {
			throw new AMapTrackException("删除终端属性失败: " + result);
		}
	}

	/**
	 * 修改该服务下的终端属性
	 */
	public void updatePropsForTerminal() {
		throw new RuntimeException("为实现的接口");
	}

	/**
	 * 列出该服务下的终端属性
	 */
	public void listPropsForTerminal() {
		throw new RuntimeException("为实现的接口");
	}

	protected Map<String, Object> getBaseParams() {
		Map<String, Object> object = new HashMap<>();
		object.put("key", client.key);
		object.put("sid", sid);

		return object;
	}

	AMapTrackClient getClient() {
		return client;
	}

	void setClient(AMapTrackClient client) {
		this.client = client;
	}

	public Integer getSid() {
		return sid;
	}

	void setSid(Integer sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isCommited() {
		return commited;
	}

	void setCommited(boolean commited) {
		this.commited = commited;
	}

	@Override
	public String toString() {
		return "Service [sid=" + sid + ", name=" + name + ", desc=" + desc + "]";
	}

}
