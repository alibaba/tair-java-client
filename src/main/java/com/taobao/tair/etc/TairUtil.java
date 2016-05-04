/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.etc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.InflaterInputStream;

public class TairUtil {
	public static final String HEXES = "0123456789ABCDEF";
	public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static void checkMalloc(int size) {
		if (size > TairConstant.TAIR_MALLOC_MAX) {
			throw new IllegalArgumentException("alloc to large byte[], size: "
					+ size);
		}
	}

	public static byte[] deflate(byte[] in) {
		ByteArrayOutputStream bos = null;

		if (in != null) {
			ByteArrayInputStream bis = new ByteArrayInputStream(in);

			bos = new ByteArrayOutputStream();

			InflaterInputStream gis;

			try {
				gis = new InflaterInputStream(bis);

				byte[] buf = new byte[8192];
				int r = -1;

				while ((r = gis.read(buf)) > 0) {
					bos.write(buf, 0, r);
				}
			} catch (IOException e) {
				bos = null;
			}
		}

		return (bos == null) ? null : bos.toByteArray();
	}

	public static String idToAddress(long id) {
		StringBuffer host = new StringBuffer(30);

		host.append((id & 0xff)).append('.');
		host.append(((id >> 8) & 0xff)).append('.');
		host.append(((id >> 16) & 0xff)).append('.');
		host.append(((id >> 24) & 0xff));

		int port = (int) ((id >> 32) & 0xffff);

		return host.append(":").append(port).toString();
	}

	public static long hostToLong(String host) {
		return hostToLong(host, -1);
	}

	public static String getHost(String address) {
		String host = null;
		if (address != null) {
			String[] a = address.split(":");
			if (a.length >= 2)
				host = a[0].trim();
		}
		return host;
	}

	public static int getPort(String address) {
		int port = 0;
		if (address != null) {
			String[] a = address.split(":");
			if (a.length >= 2)
				port = Integer.parseInt(a[1].trim());
		}
		return port;
	}

	public static long hostToLong(String host, int port) {
		if (host == null) {
			return 0;
		}

		try {
			String[] a = host.split(":");

			if (a.length >= 2) {
				port = Integer.parseInt(a[1].trim());
			}

			if (port == -1) {
				return 0;
			}

			InetSocketAddress addr = new InetSocketAddress(a[0], port);

			if ((addr == null) || (addr.getAddress() == null)
					|| (addr.getPort() == 0)) {
				return 0;
			}

			byte[] ip = addr.getAddress().getAddress();
			long address = (addr.getPort() & 0xffff);

			int ipa = 0;
			ipa |= ((ip[3] << 24) & 0xff000000);
			ipa |= ((ip[2] << 16) & 0xff0000);
			ipa |= ((ip[1] << 8) & 0xff00);
			ipa |= (ip[0] & 0xff);

			if (ipa < 0)
				address += 1;
			address <<= 32;
			return address + ipa;
		} catch (Exception e) {
		}

		return 0;
	}

	public static String toHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F))).append(" ");
		}
		return hex.toString();
	}
	
	public static String formatDate(int seconds) {
		return dateFormat.format(new Date(seconds * 1000L));
	}
	
	public static int getDuration(int expiretime) {
		int now = (int)(System.currentTimeMillis() / 1000);
		if (expiretime > now) {
			//将绝对时间转成相对时间，即使时间不同步，超时时间也是正确的
			//因为对于任何使用者而言，不管用时间点 还是时间段，其关心的都是何时超时
			expiretime -= now;
		}
		return expiretime;
	}
	
	public static boolean isValidNamespace(short namespace) {
		if ((namespace >= 0) && (namespace <= TairConstant.NAMESPACE_MAX)) {
			return true;
		}
		return false;
	}
}
