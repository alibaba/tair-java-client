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

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class RequestInvalidPacket extends RequestGetPacket {

	private String groupName;

	public RequestInvalidPacket(Transcoder transcoder, String groupName) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_REQ_INVALID_PACKET;
		this.groupName = groupName;
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
			capacity += 4;
			capacity += keyByte.length;
		}

		writePacketBegin(capacity);

		// body
		byteBuffer.put((byte) 0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(list.size());

		for (byte[] keyByte : list) {
			byteBuffer.putShort((short) keyByte.length);
			byteBuffer.put(keyByte);
		}

		writeString(getGroupName());

		writePacketEnd();

		return 0;
	}

	@Override
	public boolean decode() {
		byteBuffer.get();
		this.namespace = byteBuffer.getShort();

		int count = byteBuffer.getInt();
		byte[] dst = null;

		for (int i = 0; i < count; i++) {
			int keySize = byteBuffer.getShort();

			if (keySize > 0) {
				dst = new byte[keySize];
				byteBuffer.get(dst);
				this.keyList.add(transcoder.decode(dst));
			}
		}
		setGroupName(readString());

		return true;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
