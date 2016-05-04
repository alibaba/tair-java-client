/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.extend.packet.list.request;

import java.util.ArrayList;
import java.util.List;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.LeftOrRight;
import com.taobao.tair.packet.BasePacket;

public class RequestLRPushPacket extends BasePacket {
	private final static int HEADER_LEN = 1 + 2 + 2 + 4 + 4 + 4;
	
	private short namespace = 0;
	private short version = 0;
	private int expire = 0;
	private Object key = null;
	private List<Object> values = new ArrayList<Object>();

	private List<byte[]> bytevalues = new ArrayList<byte[]>();
	
	public RequestLRPushPacket(Transcoder transcoder, LeftOrRight lr) {
		super(transcoder);
		if (lr == LeftOrRight.IS_L)
			this.pcode = TairConstant.TAIR_REQ_LPUSH_PACKET;
		else
			this.pcode = TairConstant.TAIR_REQ_RPUSH_PACKET;
	}
	
	public RequestLRPushPacket() {
	}
	
	public void setLeftOrRight(LeftOrRight lr) {
		if (lr == LeftOrRight.IS_L)
			this.pcode = TairConstant.TAIR_REQ_LPUSH_PACKET;
		else
			this.pcode = TairConstant.TAIR_REQ_RPUSH_PACKET;
	}

	public int encode() {	
		byte[] keybytes = null;
		byte[] valuebytes = null;
		if (key == null || values.size() == 0) {
			return TairConstant.SERIALIZEERROR;
		}
		try {
			keybytes = transcoder.encode(key);
		} catch (Throwable e) {
			return TairConstant.SERIALIZEERROR;
		}
		if(keybytes == null) {
			return TairConstant.SERIALIZEERROR;
		}
		
		if(keybytes.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return TairConstant.KEYTOLARGE;
		}
		
		int valueslen = 0;
		for (Object value : values) {
			if (value == null) {
				return TairConstant.SERIALIZEERROR;
			}
			try {
				valuebytes = transcoder.encode(value);
			} catch (Throwable e) {
				return TairConstant.SERIALIZEERROR;
			}
			if (valuebytes == null) {
				return TairConstant.SERIALIZEERROR;
			}
			if(valuebytes.length > 1024*1024) {
				return TairConstant.SERIALIZEERROR;
			}
			
			if (valuebytes.length >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
				return TairConstant.VALUETOLARGE;
			}
			
			bytevalues.add(valuebytes);
			valueslen += valuebytes.length;
		}
		
		writePacketBegin(HEADER_LEN + valueslen + 4 * bytevalues.size());
		byteBuffer.put((byte)0);
		byteBuffer.putShort(namespace);
		byteBuffer.putShort((short)version);
		byteBuffer.putInt(expire);
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		byteBuffer.putInt(bytevalues.size());
		for (byte[] vb : bytevalues) {
			byteBuffer.putInt(vb.length);
			byteBuffer.put(vb);
		}
		writePacketEnd();
		
		return 0;
	}

	public boolean decode() {
		throw new UnsupportedOperationException();
	}

	public void setKey(Object key) {
		this.key = key;
	}
	public Object getKey() {
		return this.key;
	}
	
	public void addValue(Object o) {
		values.add(o);
	}
	public Object getValue(int index) {
		if (index < 0 || index >= values.size()) {
			return null;
		}
		return values.get(index);
	}
	
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}
	public short getNamespace(short namespace) {
		return this.namespace;
	}
	
	public void setVersion(short version) {
		this.version = version;
	}
	public int getVersion() {
		return this.version;
	}
	
	public void setExpire(int expire) {
		this.expire = expire;
	}
	public int getExpire() {
		return this.expire;
	}
}
