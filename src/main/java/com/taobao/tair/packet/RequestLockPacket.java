/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import com.taobao.tair.DataEntry;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.comm.Transcoder;

public class RequestLockPacket extends BasePacket {
	public static final int LOCK_STATUS = 1;
	public static final int LOCK_VALUE = 2;
	public static final int UNLOCK_VALUE = 3;
	public static final int PUT_AND_LOCK_VALUE = 4;

	private short namespace = 0;
	private int lockType = LOCK_VALUE;
	private Object key = null;
	// for PUT_AND_LOCK_VALUE
	private DataEntry value = null;

	public RequestLockPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_LOCK_PACKET;
	}

	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}

	public short getNamespace() {
		return namespace;
	}

	public void setLockType(int lockType) {
		this.lockType = lockType;
	}

	public int getLockType() {
		return this.lockType;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getKey() {
		return this.key;
	}

	public void setValue(DataEntry value) {
		this.value = value;
	}

	public DataEntry getValue() {
		return this.value;
	}

	public int encode() {
		if (lockType == PUT_AND_LOCK_VALUE) {
			return 1;
			// TODO:
		}
		byte[] keyByte = transcoder.encode(key);
		writePacketBegin(keyByte.length + 39); // keylength, 33 + other(area, locktype)
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(lockType);
		fillMetas();
		DataEntry.encodeMeta(byteBuffer); // 29
		byteBuffer.putInt(keyByte.length);// 4 
		byteBuffer.put(keyByte); // 33+length
		writePacketEnd();
		return 0;
	}
}
