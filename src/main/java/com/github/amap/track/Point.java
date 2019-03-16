package com.github.amap.track;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.github.amap.track.util.JsonUtil;

public class Point implements Serializable {
	private static final long serialVersionUID = -5097501594512162140L;
	private double x;
	private double y;
	private Long locatetime;
	//附加信息
	private final Map<String, String> props = new HashMap<>();
	
	public Point( ) {
		 
	}

	public Point(double x, double y, Long locatetime) {
		this.x = x;
		this.y = y;
		this.locatetime = locatetime;
	}
	public Point(double x, double y, Long locatetime, Map<String, String> props) {
		this.x = x;
		this.y = y;
		this.locatetime = locatetime;
		setProps(props);
	}

	// 剩下的速度这些属相暂时不实现
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public long getLocatetime() {
		return locatetime;
	}

	public void setLocatetime(Long locatetime) {
		this.locatetime = locatetime;
	}

	public String getXY() {
		BigDecimal bx = new BigDecimal(x);
		BigDecimal by = new BigDecimal(y);
		return bx.setScale(6, BigDecimal.ROUND_HALF_UP).toString() + ","
				+ by.setScale(6, BigDecimal.ROUND_HALF_UP).toString();
	}

	public void setXY(String string) {
		String[] split = string.split(",");
		x = Double.parseDouble(split[0]);
		y = Double.parseDouble(split[1]);
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

	public String toJSON() {
		Map<String, Object> object = new HashMap<>();
		object.put("location", getXY());
		object.put("locatetime", getLocatetime());
		object.put("props", JsonUtil.toJson(props));
		
		return JsonUtil.toJson(object);
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", locatetime=" + locatetime + ", props=" + props + "]";
	}
	

}
