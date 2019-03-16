package com.github.amap.track;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Points implements Serializable {
	private static final long serialVersionUID = 884788470693780644L;
	protected Trace trace;
	private Integer distance;
	private Long time;
	private List<Point> points = new ArrayList<>();

	public void add(Points p) {
		distance += p.distance;
		time += p.time;
		points.addAll(p.points);
	}
	
	
	public Integer getDistance() {
		return distance;
	}

	public Long getTime() {
		return time;
	}

	public Trace getTrace() {
		return trace;
	}

	public List<Point> getPoints() {
		return points;
	}

	void setTrace(Trace trace) {
		this.trace = trace;
	}

	void setDistance(Integer distance) {
		this.distance = distance;
	}

	void setTime(Long time) {
		this.time = time;
	}

	void setPoints(List<Point> points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return "Points [trace=" + trace + ", distance=" + distance + ", time=" + time + ", points=" + points.size() + "]";
	}
	
	
}
