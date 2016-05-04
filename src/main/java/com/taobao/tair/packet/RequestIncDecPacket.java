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
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class RequestIncDecPacket extends BasePacket {
	private short namespace = 0;
	private int count = 1;
	private int initValue = 0;
	private int expireTime = 0;
	private Object key = null;

	public RequestIncDecPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_INCDEC_PACKET;
	}

	/**
	 * encode
	 */
	public int encode() {
		byte[] keyByte = transcoder.encode(this.key);

		if (keyByte.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return 1;
		}

		writePacketBegin(keyByte.length + 40);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(count);
		byteBuffer.putInt(initValue);
		byteBuffer.putInt(expireTime);
		
		fillMetas();
		DataEntry.encodeMeta(byteBuffer);
		byteBuffer.putInt(keyByte.length);
		byteBuffer.put(keyByte);

		writePacketEnd();

		return 0;
	}

	/**
	 * decode
	 */
	public boolean decode() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the initValue
	 */
	public int getInitValue() {
		return initValue;
	}

	/**
	 * @param initValue
	 *            the initValue to set
	 */
	public void setInitValue(int initValue) {
		this.initValue = initValue;
	}

	/**
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(Object key) {
		this.key = key;
	}

	/**
	 * @return the namespace
	 */
	public short getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

}
