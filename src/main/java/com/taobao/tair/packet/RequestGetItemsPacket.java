/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.util.ArrayList;
import java.util.List;

import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class RequestGetItemsPacket extends RequestGetPacket {

	protected int count;
	protected int offset;
	protected int type;

	public RequestGetItemsPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_GETITEMS_PACKET;
	}

	@Override
	public int encode() {
		int capacity = 0;
		List<byte[]> list = new ArrayList<byte[]>();

		for (Object key : keyList) {
			byte[] keyByte = transcoder.encode(key);

			if (keyByte.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
				return 1;
			}

			list.add(keyByte);
			capacity += 40;
			capacity += keyByte.length;
		}

		writePacketBegin(capacity);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(list.size());

		for (byte[] keyByte : list) {
			fillMetas();
			DataEntry.encodeMeta(byteBuffer);
			byteBuffer.putInt(keyByte.length);
			byteBuffer.put(keyByte);
		}

		byteBuffer.putInt(count);
		byteBuffer.putInt(offset);
		byteBuffer.putInt(type);

		writePacketEnd();

		return 0;
	}

	public boolean decode() {
		throw new UnsupportedOperationException();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

}
