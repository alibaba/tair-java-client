/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.util.List;

import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.json.Json;

public class RequestAddItemsPacket extends RequestPutPacket {

	protected int maxCount;
	protected List<? extends Object> data;

	public RequestAddItemsPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_ADDITEMS_PACKET;
	}

	public int encode() {
		byte[] keyByte = transcoder.encode(key);
		byte[] dataByte = Json.serialize(data);

		if (dataByte == null)
			return 3; // serialize failed
		
		if (keyByte.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return 1;
		}

		if (dataByte.length >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
			return 2;
		}

		writePacketBegin(keyByte.length + dataByte.length);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putShort(version);
		byteBuffer.putInt(expired);
		
		fillMetas();
		DataEntry.encodeMeta(byteBuffer);
		byteBuffer.putInt(keyByte.length);
		byteBuffer.put(keyByte);

		fillMetas();
		DataEntry.encodeMeta(byteBuffer);
		byteBuffer.putInt(dataByte.length);
		byteBuffer.put(dataByte);
		
		byteBuffer.putInt(maxCount);

		writePacketEnd();

		return 0;
	}

	public boolean decode() {
		throw new UnsupportedOperationException();
	}
	
	public void setData(List<? extends Object> data) {
		this.data = data;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}
}
