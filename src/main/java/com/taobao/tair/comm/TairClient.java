/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.comm;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.WriteFuture;

import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.packet.BasePacket;


public class TairClient {

	private static final Log LOGGER = LogFactory.getLog(TairClient.class);


	private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();
	
	private static ConcurrentHashMap<Integer, ResponseCallbackTask> callbackTasks=
		new ConcurrentHashMap<Integer, ResponseCallbackTask>();
	
	private static long minTimeout=100L;
	
	private static ConcurrentHashMap<Integer, ArrayBlockingQueue<Object>> responses=
										new ConcurrentHashMap<Integer, ArrayBlockingQueue<Object>>();
	
	private final IoSession session;
	
	private String key;
	
	private TairClientFactory clientFactory;
    private static Thread callBackTaskScan = new Thread(new CallbackTasksScan());
    static{
        callBackTaskScan.setName("Thread-" + CallbackTasksScan.class.getName());
        callBackTaskScan.setDaemon(true);
        callBackTaskScan.start();
	}
	

	protected TairClient(TairClientFactory factory, IoSession session,String key) {
		this.session = session;
		this.key=key;
		this.clientFactory = factory;
	}

	public Object invoke(final BasePacket packet, final long timeout)
			throws TairClientException {
		if (isDebugEnabled) {
			LOGGER.debug("send request [" + packet.getChid() + "],time is:"
					+ System.currentTimeMillis());
		}
		ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<Object>(1);
		responses.put(packet.getChid(), queue);
		ByteBuffer bb = packet.getByteBuffer();
		bb.flip();
		byte[] data = new byte[bb.remaining()];
		bb.get(data);		
		WriteFuture writeFuture = session.write(data);
		writeFuture.addListener(new IoFutureListener() {

			public void operationComplete(IoFuture future) {
				WriteFuture wfuture = (WriteFuture) future;
				if (wfuture.isWritten()) {
					return;
				}
				String error = "send message to tair server error ["
						+ packet.getChid() + "], tair server: "
						+ session.getRemoteAddress()
						+ ", maybe because this connection closed :"
						+ !session.isConnected();
				LOGGER.warn(error);
				TairResponse response = new TairResponse();
				response.setRequestId(packet.getChid());
				response.setResponse(new TairClientException(error));
				try {
					putResponse(packet.getChid(), response.getResponse());
				} catch (TairClientException e) {
					// IGNORE,should not happen
				}
				// close this session
				if(session.isConnected())
					session.close();
				else
					clientFactory.removeClient(key);
			}
			

		});
		Object response = null;
		try {
			response = queue.poll(timeout, TimeUnit.MILLISECONDS);
			if (response == null) {
				throw new TairClientException(
						"tair client invoke timeout,timeout is: " + timeout
								+ ",requestId is: " + packet.getChid() + "request type:" + packet.getClass().getName());
			} else if (response instanceof TairClientException) {
				throw (TairClientException) response;
			}
		} catch (InterruptedException e) {
			throw new TairClientException("tair client invoke error", e);
		} finally {
			responses.remove(packet.getChid());
			// For GC
			queue = null;
		}
		if (isDebugEnabled) {
			LOGGER.debug("return response [" + packet.getChid() + "],time is:"
					+ System.currentTimeMillis());
			LOGGER.debug("current responses size: " + responses.size());
		}
		
		// do decode here
		if (response instanceof BasePacket) {
			((BasePacket)response).decode();
		}
		return response;
	}
	

	public void invokeAsync(final BasePacket packet, final long timeout,ResponseListener listener){
		if(isDebugEnabled){
			LOGGER.debug("send request ["+packet.getChid()+"] async,time is:"+System.currentTimeMillis());
		}
		if(minTimeout>timeout){
			minTimeout=timeout;
		}
		final ResponseCallbackTask callbackTask=new ResponseCallbackTask(packet.getChid(),listener, this.session, timeout);
		callbackTasks.put(packet.getChid(), callbackTask);
		
		ByteBuffer bb = packet.getByteBuffer();
		bb.flip();
		byte[] data = new byte[bb.remaining()];
		bb.get(data);
		WriteFuture writeFuture=session.write(data);
		writeFuture.addListener(new IoFutureListener(){

			public void operationComplete(IoFuture future) {
				WriteFuture wfuture=(WriteFuture)future;
				if(wfuture.isWritten()){
					return;
				}
				String error = "send message to tair server error [" + packet.getChid() + "], tair server: " + session.getRemoteAddress()+", maybe because this connection closed :"+ !session.isConnected();
	            LOGGER.warn(error);
	            callbackTask.setResponse(new TairClientException(error));
	            
				// close this session
				if(session.isConnected())
					session.close();
				else
					clientFactory.removeClient(key);
			}
			
		});
	}

	protected void putResponse(Integer requestId, Object response)
			throws TairClientException {
		if (responses.containsKey(requestId)) {
			try {
				ArrayBlockingQueue<Object> queue = responses.get(requestId);
				if (queue != null) {
					queue.put(response);
					if (isDebugEnabled) {
						LOGGER.debug("put response [" + requestId
								+ "],time is:" + System.currentTimeMillis());
					}
				} else if (isDebugEnabled) {
					LOGGER.debug("give up the response,maybe because timeout,requestId is:"
									+ requestId);
				}
				
			} catch (InterruptedException e) {
				throw new TairClientException("put response error", e);
			}
		} else {
			if (isDebugEnabled)
				LOGGER
						.debug("give up the response,maybe because timeout,requestId is:"
								+ requestId);
		}
	}
	
	protected boolean isCallbackTask(Integer requestId){
		return callbackTasks.containsKey(requestId);
	}

	protected void putCallbackResponse(Integer requestId,Object response) throws TairClientException{
		ResponseCallbackTask task=callbackTasks.get(requestId);
		if(task==null)
			return;
		task.setResponse(response);
	}

	static class CallbackTasksScan implements Runnable{

		static final long DEFAULT_SLEEPTIME=10L;
		
		boolean isRunning=true;
		
		final TairClientException timeoutException=new TairClientException("receive response timeout");
		
		public void run() {
			while(isRunning){
				List<Integer> removeIds=new ArrayList<Integer>();
				for (Entry<Integer, ResponseCallbackTask> entry: callbackTasks.entrySet()) {
					long currentTime=System.currentTimeMillis();
					ResponseCallbackTask task=entry.getValue();
					if((task.getIsDone().get())){
						removeIds.add(task.getRequestId());
					}
					else if(task.getTimeout() < currentTime){
						removeIds.add(task.getRequestId());
						task.setResponse(timeoutException);
					}
				}
				for (Integer removeId : removeIds) {
					callbackTasks.remove(removeId);
				}
				long sleepTime=DEFAULT_SLEEPTIME;
				if(callbackTasks.size()==0){
					sleepTime=minTimeout;
				}
				try {
					Thread.sleep(sleepTime);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public String toString() {
		if (this.session != null)
			return this.session.toString();
		return "null session client";
	}
	
	public void close() {
		if (session != null) {
			session.close();
		}
		if (responses != null) {
			responses.clear();
		}
	}

}
