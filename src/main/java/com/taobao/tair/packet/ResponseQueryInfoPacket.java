/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.util.*;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class ResponseQueryInfoPacket extends BasePacket{
	
	private Map<String, String>  kv ;
	
	public ResponseQueryInfoPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_RESP_QUERY_INFO_PACKET;
	}

	/**
	 * encode
	 */
	public int encode() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * decode
	 */
	public boolean decode() {
		this.kv = new HashMap<String,String>();
		int count = byteBuffer.getInt();
		for (int i = 0; i < count; i++) {
			String name = readString();
			String value = readString();
			kv.put(name, value);
		}
		return true;
	}
	
	/**
	 *  get map
	 */
	public Map<String, String> getKv() {
		return kv;
	}

}
