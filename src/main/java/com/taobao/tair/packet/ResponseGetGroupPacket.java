/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;

public class ResponseGetGroupPacket extends BasePacket {
	private int configVersion;
	private int copyCount;
	private int bucketCount;
	private List<Long> serverList;
	private Map<String, String> configMap;
	private Set<Long> aliveNodes;

	public ResponseGetGroupPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_RESP_GET_GROUP_NEW_PACKET;
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
		this.serverList = new ArrayList<Long>();
		this.configMap = new HashMap<String, String>();

		bucketCount = byteBuffer.getInt();
		copyCount = byteBuffer.getInt();
		this.configVersion = byteBuffer.getInt();

		// get config items
		int count = byteBuffer.getInt();
		for (int i = 0; i < count; i++) {
			String name = readString();
			String value = readString();
			configMap.put(name, value);
		}

		// get server list
		count = byteBuffer.getInt();
		if (count > 0) {
			byte[] b = new byte[count];
			byteBuffer.get(b);
			byte[] result = TairUtil.deflate(b);
			ByteBuffer buff = ByteBuffer.wrap(result);
			buff.order(ByteOrder.LITTLE_ENDIAN);

			List<Long> ss = new ArrayList<Long>();
			boolean valid = false;
			int c = 0;
			while (buff.hasRemaining()) {
				long sid = buff.getLong();
				if (!valid) {
					valid = sid != 0;
				}
				ss.add(sid);
				c++;
				if (c == bucketCount) {
					if (valid) {
						serverList.addAll(ss);
						ss = new ArrayList<Long>();
					}
					c = 0;
					valid = false;
				}
			}
		}
		
		aliveNodes = new HashSet<Long>();
		count = byteBuffer.getInt();
		for(int i=0; i<count; i++)
			aliveNodes.add(byteBuffer.getLong());

		return true;
	}

	/**
	 * 
	 * @return the configVersion
	 */
	public int getConfigVersion() {
		return configVersion;
	}

	/**
	 * 
	 * @param configVersion
	 *            the configVersion to set
	 */
	public void setConfigVersion(int configVersion) {
		this.configVersion = configVersion;
	}

	public List<Long> getServerList() {
		return serverList;
	}

	public Map<String, String> getConfigMap() {
		return configMap;
	}

	public int getCopyCount() {
		return copyCount;
	}

	public int getBucketCount() {
		return bucketCount;
	}

	public Set<Long> getAliveNodes() {
		return aliveNodes;
	}

	public void setAliveNodes(Set<Long> aliveNodes) {
		this.aliveNodes = aliveNodes;
	}
	
}
