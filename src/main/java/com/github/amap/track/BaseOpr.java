package com.github.amap.track;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.github.amap.track.util.HttpUtil;

public class BaseOpr implements Serializable {
	private static final long serialVersionUID = 5940916603997502761L;

	protected String postForm(String url, Map<String, Object> queryMap) {
		String result = HttpUtil.post(url, new HashMap<>(queryMap));
		return result;
	}

	protected String get(String url, Map<String, Object> queryMap) {
		StringBuilder sb = new StringBuilder();
		queryMap.forEach((k, v) -> {
			sb.append(k + "=" + String.valueOf(v) + "&");
		});
		sb.deleteCharAt(sb.length() - 1);
		url = url + "?" + sb.toString();
		String result = HttpUtil.get(url);
		return result;
	}

}
