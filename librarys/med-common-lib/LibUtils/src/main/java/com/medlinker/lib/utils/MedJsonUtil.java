package com.medlinker.lib.utils;

import java.util.Map;
import java.util.Map.Entry;

public class MedJsonUtil {

	public static String mapToJson(Map<String,String> map) {
		if (map == null || map.isEmpty())
			return "";
		StringBuilder sBuffer = new StringBuilder();
		sBuffer.append("{");
		for (Entry<String, String> entry : map.entrySet()) {
			sBuffer.append("\"");
			sBuffer.append(entry.getKey());
			sBuffer.append("\":");
			sBuffer.append("\"");
			sBuffer.append(entry.getValue());
			sBuffer.append("\"");
			sBuffer.append(",");
		}
		sBuffer.deleteCharAt(sBuffer.lastIndexOf(","));
		sBuffer.append("}");
		return sBuffer.toString();
	}

}
