/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.extend.packet.list.request;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.LeftOrRight;
import com.taobao.tair.packet.BasePacket;

public class RequestLRPopPacket extends BasePacket {
	
	private final static int HEADER_LEN = 1 + 2 + 2 + 4 + 4 + 4;
	
	private short namespace = 0;
	private short version = 0;
	private int expire = 0;
	private Object key = null;
	private int count = 1;
	
	public RequestLRPopPacket(Transcoder transcoder, LeftOrRight lr) {
		super(transcoder);
		count = 1;
		if (lr == LeftOrRight.IS_L)
			this.pcode = TairConstant.TAIR_REQ_LPOP_PACKET;
		else
			this.pcode = TairConstant.TAIR_REQ_RPOP_PACKET;
	}
	
	public RequestLRPopPacket() {
	}
	
	public void setLeftOrRight(LeftOrRight lr) {
		if (lr == LeftOrRight.IS_L)
			this.pcode = TairConstant.TAIR_REQ_LPOP_PACKET;
		else
			this.pcode = TairConstant.TAIR_REQ_RPOP_PACKET;
	}


	public int encode() {	
		byte[] keybytes = null;
		if (key == null) {
			return TairConstant.SERIALIZEERROR;
		}
		try {
			keybytes = transcoder.encode(key);
		} catch (Throwable e) {
			return TairConstant.SERIALIZEERROR;
		}
		if (keybytes == null) {
			return TairConstant.SERIALIZEERROR;
		}
		
		if(keybytes.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return TairConstant.KEYTOLARGE;
		}
		
		writePacketBegin(keybytes.length + HEADER_LEN);
		byteBuffer.put((byte) 0); // 1 byte push server flag
		byteBuffer.putShort(namespace);  // 2 byte push ns 
		byteBuffer.putShort(version);    // 2
		byteBuffer.putInt(expire);       // 4
		byteBuffer.putInt(count);			// 4 byte count
		byteBuffer.putInt(keybytes.length);// 4 byte length
		byteBuffer.put(keybytes); // keybytes.length
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
	
	public void setCount(int count) {
		this.count = count;
	}
	public int getCount() {
		return this.count;
	}
	
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}
	public int getNamespace() {
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
