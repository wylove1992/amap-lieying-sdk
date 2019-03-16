package com.github.amap.track.util;

import com.alibaba.fastjson.JSON;

public class JsonUtil {

	/**
	 * 对象转json字符串
	 * 
	 * @param obj 对象
	 * @return json字符串
	 */
	public static String toJson(Object obj) {
		try {
			return JSON.toJSONString(obj);
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

	/**
	 * json字符串转对象
	 * 
	 * @param json  json字符串
	 * @param clazz 对象类型
	 * @return 对象
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			return JSON.parseObject(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("", e);
		}
	}

}
