/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.etc;

import java.io.Serializable;

public class IncData implements Serializable {
	private static final long serialVersionUID = -979272731713318985L;
	private int count;

	public IncData(int count) {
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public static byte[] encode(IncData incData) {
		// Tair server cope with IncData by little-endian(dependable)
		byte[] b = new byte[4];
		int number = incData.getCount();
		b[0] = (byte) (number & 0xFF);
		b[1] = (byte) ((number >> 8) & 0xFF);
		b[2] = (byte) ((number >> 16) & 0xFF);
		b[3] = (byte) ((number >> 24) & 0xFF);
		return b;
	}

	// Tair server cope with IncData by little-endian(dependable)
	public static int decode(byte[] b) {
		int rv	 = 0;
		int bits = 0;

		for (byte i : b) {
			rv |= (((i < 0) ? (256 + i)
					: i) << bits);
			bits += 8;
		}
		return rv;
	}
}