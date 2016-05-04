/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.comm;

import org.apache.mina.common.IoSession;

import com.taobao.tair.etc.TairClientException;

public interface ResponseListener {

	public void responseReceived(Object response);
	
	public void exceptionCaught(IoSession session, TairClientException exception);
	
}
