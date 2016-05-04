/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.comm;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.packet.PacketStreamer;

public class TairClientFactory {

	private static final Log LOGGER = LogFactory.getLog(TairClientFactory.class);

	private static final int processorCount = Runtime.getRuntime()
			.availableProcessors() + 1;

	private static final String CONNECTOR_THREADNAME = "TAIRCLIENT";

	// daemon thread
	private static final ThreadFactory CONNECTOR_TFACTORY = new NamedThreadFactory(
			CONNECTOR_THREADNAME, false);

	private static final TairClientFactory factory = new TairClientFactory();
	
	private static final int MIN_CONN_TIMEOUT = 1000;

	private final SocketConnector ioConnector;

	private final ConcurrentHashMap<String, FutureTask<TairClient>> clients = new ConcurrentHashMap<String, FutureTask<TairClient>>();

	public TairClientFactory() {
		ioConnector = new SocketConnector(processorCount, Executors
				.newCachedThreadPool(CONNECTOR_TFACTORY));
	}

	public static TairClientFactory getSingleInstance() {
		return factory;
	}

	public void close() {
		for (FutureTask<TairClient> task : clients.values()) {	
			if (task.isDone() || !task.cancel(true)) {
				TairClient client = null;
				try {
					client = task.get();
				} catch (InterruptedException e) {
					LOGGER.warn(e);
				} catch (ExecutionException e) {
					LOGGER.warn(e);
				} catch (CancellationException e){
				}
				client.close();
			}
		}
		clients.clear();
	}
	public TairClient get(final String targetUrl, final int connectionTimeout, final PacketStreamer pstreamer)
			throws TairClientException {
		String key = targetUrl;
		FutureTask<TairClient> existTask = null;
		existTask = clients.get(key);
 
		if (existTask == null) {
			FutureTask<TairClient> task = new FutureTask<TairClient>(
					new Callable<TairClient>() {
						public TairClient call() throws Exception {
							return createClient(targetUrl, connectionTimeout,
									pstreamer);
						}
					});
			existTask = clients.putIfAbsent(key, task);
			if (existTask == null) {
				existTask = task;
				task.run();
			}
		}
		
		try {
			return existTask.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (CancellationException e) {
			// cancel exception may be lost connection fd, but we never called task.cancel();
			clients.remove(key);
			throw e;
		} catch (ExecutionException e) {
			// create socket failed, so need not close
			clients.remove(key);
			throw new TairClientException(
					"create socket exception, target address is "+ targetUrl, e);
		}
	}

	protected void removeClient(String key) {
		clients.remove(key);
	}

	private synchronized TairClient createClient(String targetUrl, int connectionTimeout, PacketStreamer pstreamer)
			throws Exception {
		SocketConnectorConfig cfg = new SocketConnectorConfig();
		cfg.setThreadModel(ThreadModel.MANUAL);
		if (connectionTimeout < MIN_CONN_TIMEOUT)
			connectionTimeout = MIN_CONN_TIMEOUT;
		cfg.setConnectTimeout((int) connectionTimeout / 1000);
		cfg.getSessionConfig().setTcpNoDelay(true);
		cfg.getFilterChain().addLast("objectserialize",
				new TairProtocolCodecFilter(pstreamer));
		String address = TairUtil.getHost(targetUrl);
		int port = TairUtil.getPort(targetUrl);
		SocketAddress targetAddress = new InetSocketAddress(address, port);
		TairClientProcessor processor = new TairClientProcessor();
		ConnectFuture connectFuture = ioConnector.connect(targetAddress, null,
				processor, cfg);

		connectFuture.join();
		
		IoSession ioSession = connectFuture.getSession();
		if ((ioSession == null) || (!ioSession.isConnected())) {
			throw new Exception(
					"create tair connection error,targetaddress is "
							+ targetUrl);
		}
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("create tair connection success,targetaddress is "
					+ targetUrl);
		}
		TairClient client = new TairClient(this, ioSession,targetUrl);
		processor.setClient(client);
		processor.setFactory(this, targetUrl);
		return client;
	}

}
