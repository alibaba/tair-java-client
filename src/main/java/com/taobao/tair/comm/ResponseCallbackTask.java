/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.comm;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.mina.common.IoSession;

import com.taobao.tair.etc.TairClientException;

public class ResponseCallbackTask {

	private Integer requestId;
	
	private ResponseListener listener;
	
	private AtomicBoolean isDone=new AtomicBoolean(false);
	
	private IoSession iosession;
	
	private long timeout;
	
	public ResponseCallbackTask(Integer requestId,ResponseListener listener,IoSession session, long timeout){
		this.requestId=requestId;
		this.listener=listener;
		this.iosession = session;
		this.timeout=System.currentTimeMillis()+timeout;
	}

	public Integer getRequestId() {
		return requestId;
	}

	public ResponseListener getListener() {
		return listener;
	}
	
	public long getTimeout() {
		return timeout;
	}

	public AtomicBoolean getIsDone() {
		return isDone;
	}

	public void setResponse(Object response) {
		if(!isDone.compareAndSet(false, true)){
			return;
		}
		if(response instanceof TairClientException){
			listener.exceptionCaught(this.iosession, (TairClientException) response);
		}
		else{
			listener.responseReceived(response);
		}
	}
	
}
