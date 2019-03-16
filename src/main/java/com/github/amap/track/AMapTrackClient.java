package com.github.amap.track;

import java.util.Iterator;
import java.util.List;

/**
 * 表示客户端对象，用户通过key来实例化这个类，这个类可以管理服务（Service）
 * 
 * @author wy
 *
 */
public class AMapTrackClient {

	protected String key;

	public AMapTrackClient(String key) {
		this.key = key;
	}

	/**
	 * 新建一个Service
	 * 
	 * @param name
	 * @param desc
	 * @return Service
	 */
	public Service createService(String name, String desc) {
		Service opr = new Service();
		opr.setClient(this);
		opr.setName(name);
		opr.setDesc(desc);
		opr.add();
		return opr;
	}

	/**
	 * 通过sid或者name查找service
	 * 
	 * @param sid  可为空
	 * @param name 可为空
	 * @return Service
	 */
	public Service getService(Integer sid, String name) {
		Iterator<Service> iterator = listService().iterator();
		while (iterator.hasNext()) {
			Service serviceOpr = (Service) iterator.next();
			if (sid != null && serviceOpr.getSid().intValue() == sid.intValue())
				return serviceOpr;
			if (serviceOpr.getName().equals(name))
				return serviceOpr;
		}
		return null;
	}

	/**
	 * 列出当前key下面所有的Service
	 * 
	 * @return List<Service>
	 */
	public List<Service> listService() {
		Service opr = new Service();
		opr.setClient(this);
		return opr.list();
	}

}
