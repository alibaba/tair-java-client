/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Json {

	private static final int ELEMENT_TYPE_INT = 0;
	private static final int ELEMENT_TYPE_LLONG = 1;
	private static final int ELEMENT_TYPE_DOUBLE = 2;
	private static final int ELEMENT_TYPE_STRING = 3;
	public static final int ELEMENT_TYPE_INVALID = 4;
	private static final String charset = "UTF-8";

	public static byte[] serialize(List<? extends Object> data) {
		int type = -1;
		if (data == null || data.size() == 0
				|| (type = checkType(data)) < 0)
			return null;

		String str = JSONValue.toJSONString(data);

		try {
			byte[] bytes = str.getBytes(charset);
			int meta = encodeMeta(data.size(), type);
			ByteBuffer bb = ByteBuffer.allocate(4 + bytes.length);
			bb.putInt(meta);
			bb.put(bytes);

			return bb.array();
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static List<? extends Object> deSerialize(byte[] source)
			throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(source);
		int meta = buffer.getInt();
		int type = getType(meta);
		String value = new String(source, 4, source.length - 4, charset);

		switch (type) {
		case ELEMENT_TYPE_INT:
			List<Integer> resInts = new ArrayList<Integer>();
			JSONArray arrayInts = (JSONArray) JSONValue.parse(value);
			for (Object object : arrayInts) {
				resInts.add(Integer.parseInt(object.toString()));
			}
			return resInts;
		case ELEMENT_TYPE_LLONG:
			List<Long> resLongs = new ArrayList<Long>();
			JSONArray arrayLongs = (JSONArray) JSONValue.parse(value);
			for (Object object : arrayLongs) {
				resLongs.add(Long.parseLong(object.toString()));
			}
			return resLongs;
		case ELEMENT_TYPE_DOUBLE:
			List<Double> resDoubles = new ArrayList<Double>();
			JSONArray arrayDoubles = (JSONArray) JSONValue.parse(value);
			for (Object object : arrayDoubles) {
				resDoubles.add(Double.parseDouble(object.toString()));
			}
			return resDoubles;
		case ELEMENT_TYPE_STRING:
			List<String> results = new ArrayList<String>();
			JSONArray arrayString = (JSONArray) JSONValue.parse(value);
			for (Object object : arrayString)
				results.add((String) object);
			return results;
		default:
			break;
		}

		return null;
	}

	public static int checkType(List<? extends Object> elements) {
		int firstType = -1;
		
		for (int i=0; i<elements.size(); ++i) {
			if (i == 0) {
				firstType = checkType(elements.get(i));
				if (firstType < 0)
					return firstType;
			} else {
				int type = checkType(elements.get(i));
				if (type < 0 || type != firstType) {
					return -1;
				}
			}
			
		}
		return firstType;
	}
	
	private static int checkType(Object element) {
		if (element instanceof Integer)
			return ELEMENT_TYPE_INT;

		if (element instanceof Long)
			return ELEMENT_TYPE_LLONG;

		if (element instanceof Double)
			return ELEMENT_TYPE_DOUBLE;

		if (element instanceof String)
			return ELEMENT_TYPE_STRING;

		return -1;
	}

	private static int encodeMeta(int count, int type) {
		int result = (type & 0x7) << 16;
		result |= (count & 0xFFFF);
		return result;
	}

	@SuppressWarnings("unused")
	private static int getCount(int value) {
		return value & 0xFFFF;
	}

	private static int getType(int value) {
		return (value >> 16) & 0x7;
	}
}
