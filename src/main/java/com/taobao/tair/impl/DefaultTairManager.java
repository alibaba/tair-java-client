/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;
import com.taobao.tair.TairManager;
import com.taobao.tair.comm.DefaultTranscoder;
import com.taobao.tair.comm.MultiSender;
import com.taobao.tair.comm.TairClient;
import com.taobao.tair.comm.TairClientFactory;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.IncData;
import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.json.Json;
import com.taobao.tair.packet.BasePacket;
import com.taobao.tair.packet.RequestAddItemsPacket;
import com.taobao.tair.packet.RequestCommandCollection;
import com.taobao.tair.packet.RequestGetAndRemoveItemsPacket;
import com.taobao.tair.packet.RequestGetItemsCountPacket;
import com.taobao.tair.packet.RequestGetItemsPacket;
import com.taobao.tair.packet.RequestGetPacket;
import com.taobao.tair.packet.RequestIncDecPacket;
import com.taobao.tair.packet.RequestLockPacket;
import com.taobao.tair.packet.RequestPutPacket;
import com.taobao.tair.packet.RequestRemoveItemsPacket;
import com.taobao.tair.packet.RequestRemovePacket;
import com.taobao.tair.packet.ResponseGetItemsPacket;
import com.taobao.tair.packet.ResponseGetPacket;
import com.taobao.tair.packet.ResponseIncDecPacket;
import com.taobao.tair.packet.ReturnPacket;
import com.taobao.tair.packet.TairPacketStreamer;

public class DefaultTairManager implements TairManager {
	private static final Log log = LogFactory.getLog(DefaultTairManager.class);
	protected static String clientVersion = "TairClient 2.3.5";
	protected List<String> configServerList = null;
	protected String groupName = null;
	protected ConfigServer configServer = null;
	protected MultiSender multiSender = null;
	protected int timeout = TairConstant.DEFAULT_TIMEOUT;
	protected int maxWaitThread = TairConstant.DEFAULT_WAIT_THREAD;
	protected TairPacketStreamer packetStreamer = null;
	protected Transcoder transcoder = null;
	protected int compressionThreshold = 0;
	protected String charset = null;
	protected String name = null;
	protected AtomicInteger failCounter = new AtomicInteger(0);

	protected TairClientFactory clientFactory = null;
	
	public DefaultTairManager() {
		this("DefaultTairManager", true);
	}

	public DefaultTairManager(String name, boolean sharedFactory) {
		this.name = name;
		if (sharedFactory)
			clientFactory = TairClientFactory.getSingleInstance();
		else
			clientFactory = new TairClientFactory();
	}
	
	/**
	 * 鏇存柊鏈嶅姟鍒楄〃
	 */
	protected void updateConfigServer(){
		configServer.checkConfigVersion(0);
		failCounter.set(0);
	}

	public void init() {
		transcoder = new DefaultTranscoder(compressionThreshold, charset);
		packetStreamer = new TairPacketStreamer(transcoder);
		configServer = new ConfigServer(clientFactory, groupName, configServerList,
										packetStreamer);
		if (!configServer.retrieveConfigure()) {
			throw new RuntimeException(clientVersion + ": init config failed");
		}
		multiSender = new MultiSender(clientFactory, packetStreamer);
		log.warn(name + " [" + getVersion() + "] started...");
		
	}

	public void close() {
		if (clientFactory != null) {
			clientFactory.close();
		}
	}
	private TairClient getClient(Object key, boolean isRead) {
		long address = configServer.getServer(transcoder.encode(key), isRead);
		
		if (address == 0)
			return null;

		String host = TairUtil.idToAddress(address);
		if (host != null) {
			try {
				return clientFactory.get(host, timeout, packetStreamer);
			} catch (TairClientException e) {
				log.error("getClient failed " + host, e);
			}
		}
		return null;
	}

	private BasePacket sendRequest(Object key, BasePacket packet) {
		return sendRequest(key, packet, false);
	}

	protected BasePacket sendRequest(Object key, BasePacket packet, boolean isRead) {
		TairClient client = getClient(key, isRead);

		if (client == null) {
			int value = failCounter.incrementAndGet();

			if (value > 100) {
				configServer.checkConfigVersion(0);
				failCounter.set(0);
				log.warn("connection failed happened 100 times, sync configuration");
			}

			log.warn("conn is null ");
			return null;
		}

		
		BasePacket returnPacket = null;
		long startTime = System.currentTimeMillis();

		try {
			returnPacket = (BasePacket) client.invoke(packet, timeout);
		} catch (TairClientException e) {
			log.error("send request to " + client + " failed ", e);
		}
		long endTime = System.currentTimeMillis();

		if (returnPacket == null) {

			if (failCounter.incrementAndGet() > 100) {
				configServer.checkConfigVersion(0);
				failCounter.set(0);
				log.warn("connection failed happened 100 times, sync configuration");
			}

			return null;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("key=" + key + ", timeout: " + timeout + ", used: " + (endTime - startTime)
						  + " (ms), client: " + client);
			}
		}	
		return returnPacket;
 
	}

	public Result<Integer> decr(int namespace, Serializable key, int value,
								int defaultValue, int expireTime) {
		if (value < 0){
			return new Result<Integer>(ResultCode.ITEMSIZEERROR);
		}
		
		return addCount(namespace, key, -value, defaultValue, expireTime);
	}

	public ResultCode delete(int namespace, Serializable key) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		
		if (key == null) {
			return ResultCode.SERIALIZEERROR;
		}
		RequestRemovePacket packet = new RequestRemovePacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			rc = ResultCode.valueOf(((ReturnPacket) returnPacket).getCode());
		}			
		return rc;
	}

	public ResultCode invalid(int namespace, Serializable key) {
		return delete(namespace, key);
	}

	public ResultCode minvalid(int namespace, List<? extends Object> keys) {
		return mdelete(namespace, keys);
	}

	public Result<DataEntry> get(int namespace, Serializable key) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<DataEntry>(ResultCode.NSERROR);
		}
		RequestGetPacket packet = new RequestGetPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<DataEntry>(ResultCode.KEYTOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet, true);

		if ((returnPacket != null) && returnPacket instanceof ResponseGetPacket) {
			ResponseGetPacket r = (ResponseGetPacket) returnPacket;

			DataEntry resultObject = null;

			List<DataEntry> entryList = r.getEntryList();

			rc = ResultCode.valueOf(r.getResultCode());
			if (rc == ResultCode.SUCCESS && entryList.size() > 0){
				resultObject = entryList.get(0);
			}

			configServer.checkConfigVersion(r.getConfigVersion());
			return new Result<DataEntry>(rc, resultObject);
		}
		return new Result<DataEntry>(rc);
	}

	public String getVersion() {
		return clientVersion;
	}

	public Result<Integer> incr(int namespace, Serializable key, int value,
								int defaultValue, int expireTime) {
		if (value < 0){
			return new Result<Integer>(ResultCode.ITEMSIZEERROR);
		}

		return addCount(namespace, key, value, defaultValue, expireTime);		
	}

	public ResultCode setCount(int namespace, Serializable key, int count) {
		return setCount(namespace, key, count, 0, 0);
	}

	public ResultCode setCount(int namespace, Serializable key, int count, int version, int expireTime) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		RequestPutPacket packet = new RequestPutPacket(transcoder);

		IncData value = new IncData(count);
		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setData(value);
		packet.setVersion((short) version);
		packet.setExpired(expireTime);

		// set flag implicitly
		int ec = packet.encode(DataEntry.TAIR_ITEM_FLAG_ADDCOUNT);

		if (ec == TairConstant.KEYTOLARGE) {
			return ResultCode.KEYTOLARGE;
		} else if (ec == TairConstant.VALUETOLARGE) {
			return ResultCode.VALUETOLARGE;
		} else if (ec == TairConstant.SERIALIZEERROR) {
			return ResultCode.SERIALIZEERROR;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;

			if (log.isDebugEnabled()) {
				log.debug("get return packet: " + returnPacket + ", code="
						  + r.getCode() + ", msg=" + r.getMsg());
			}

			rc = ResultCode.valueOf(r.getCode());

			configServer.checkConfigVersion(r.getConfigVersion());
		}
		return rc;
	}

	private Result<Integer> addCount(int namespace, Serializable key, int value,
									 int defaultValue, int expireTime) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<Integer>(ResultCode.NSERROR);
		}
		RequestIncDecPacket packet = new RequestIncDecPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setCount(value);
		packet.setInitValue(defaultValue);
		packet.setExpireTime(expireTime);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<Integer>(ResultCode.KEYTOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null)) {
			if (returnPacket instanceof ResponseIncDecPacket) {
				ResponseIncDecPacket r = (ResponseIncDecPacket) returnPacket;
				rc = ResultCode.SUCCESS;
				configServer.checkConfigVersion(r.getConfigVersion());
				return new Result<Integer>(rc, r.getValue());
			} else if (returnPacket instanceof ReturnPacket) {
				ReturnPacket rp = (ReturnPacket) returnPacket;
				rc = ResultCode.valueOf(rp.getCode());
				configServer.checkConfigVersion(rp.getConfigVersion());
			}
		}
		return new Result<Integer>(rc);
	}

	public ResultCode mdelete(int namespace, List<? extends Object> keys) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		RequestCommandCollection rcc = new RequestCommandCollection();

		for (Object key : keys) {
			long address = configServer
				.getServer(transcoder.encode(key), false);

			if (address == 0) {
				continue;
			}

			RequestRemovePacket packet = (RequestRemovePacket) rcc
				.findRequest(address);

			if (packet == null) {
				packet = new RequestRemovePacket(transcoder);
				packet.setNamespace((short) namespace);
				packet.addKey(key);
				rcc.addRequest(address, packet);
			} else {
				packet.addKey(key);
			}
		}

		for (BasePacket p : rcc.getRequestCommandMap().values()) {
			RequestGetPacket rp = (RequestGetPacket) p;
			// check key size
			int ec = rp.encode();

			if (ec == 1) {
				log.error("key too larget: ");
				return ResultCode.KEYTOLARGE;
			}
		}

		ResultCode rc = ResultCode.CONNERROR;
		boolean ret = multiSender.sendRequest(rcc, timeout);
		if (ret) {
			int maxConfigVersion = 0;

			rc = ResultCode.SUCCESS;
			for (BasePacket rp : rcc.getResultList()) {
				if (rp instanceof ReturnPacket) {
					ReturnPacket returnPacket = (ReturnPacket) rp;
					returnPacket.decode();

					if (returnPacket.getConfigVersion() > maxConfigVersion) {
						maxConfigVersion = returnPacket.getConfigVersion();
					}

					ResultCode drc = ResultCode.valueOf(returnPacket.getCode());
					if (drc.isSuccess() == false) {
						log.debug("mdelete not return success, result code: " + ResultCode.valueOf(returnPacket.getCode()));
						rc = ResultCode.PARTSUCC;
					}
				}
			}

			configServer.checkConfigVersion(maxConfigVersion);
		}else{
			//鍦ㄥ苟鍙戣姹�鍑虹幇闂 璁板綍閿欒鏁伴噺(鍘熷厛杩欎釜缂哄け)
			if (failCounter.incrementAndGet() > 100) {
				configServer.checkConfigVersion(0);
				failCounter.set(0);
				log.warn("connection failed happened 100 times, sync configuration");
			}
		}

		return rc;
	}

	public Result<List<DataEntry>> mget(int namespace,
										List<? extends Object> keys) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<List<DataEntry>>(ResultCode.NSERROR);
		}
		RequestCommandCollection rcc = new RequestCommandCollection();

		for (Object key : keys) {
			long address = configServer.getServer(transcoder.encode(key), true);

			if (address == 0) {
				continue;
			}

			RequestGetPacket packet = (RequestGetPacket) rcc
				.findRequest(address);

			if (packet == null) {
				packet = new RequestGetPacket(transcoder);
				packet.setNamespace((short) namespace);
				packet.addKey(key);
				rcc.addRequest(address, packet);
			} else {
				packet.addKey(key);
			}
		}

		int reqSize = 0;

		for (BasePacket p : rcc.getRequestCommandMap().values()) {
			RequestGetPacket rp = (RequestGetPacket) p;

			// calculate uniq key number
			reqSize += rp.getKeyList().size();

			// check key size
			int ec = rp.encode();

			if (ec == 1) {
				log.error("key too larget: ");
				return new Result<List<DataEntry>>(ResultCode.KEYTOLARGE);
			}
		}

		boolean ret = multiSender.sendRequest(rcc, timeout);

		if (!ret) {
			//鍦ㄥ苟鍙戣姹�鍑虹幇闂 璁板綍閿欒鏁伴噺(鍘熷厛杩欎釜缂哄け)
			if (failCounter.incrementAndGet() > 100) {
				configServer.checkConfigVersion(0);
				failCounter.set(0);
				log.warn("connection failed happened 100 times, sync configuration");
			}
			
			return new Result<List<DataEntry>>(ResultCode.CONNERROR);
		}

		List<DataEntry> results = new ArrayList<DataEntry>();

		ResultCode rc = ResultCode.SUCCESS;
		ResponseGetPacket resp = null;

		int maxConfigVersion = 0;
		for (BasePacket bp : rcc.getResultList()) {
			if (bp instanceof ResponseGetPacket) {
				resp = (ResponseGetPacket) bp;
				resp.decode();
				results.addAll(resp.getEntryList());

				// calculate max config version
				if (resp.getConfigVersion() > maxConfigVersion) {
					maxConfigVersion = resp.getConfigVersion();
				}
			} else {
				log.warn("receive wrong packet type: " + bp);
			}
		}

		configServer.checkConfigVersion(maxConfigVersion);

		if (results.size() == 0) {
			rc = ResultCode.DATANOTEXSITS;
		} else if (results.size() != reqSize) {
			if (log.isDebugEnabled()) {
				log.debug("mget partly success: request key size: " + reqSize
						  + ", get " + results.size());
			}
			rc = ResultCode.PARTSUCC;
		}

		return new Result<List<DataEntry>>(rc, results);
	}

	public ResultCode put(int namespace, Serializable key, Serializable value) {
		return put(namespace, key, value, 0, 0);
	}

	public ResultCode put(int namespace, Serializable key, Serializable value,
						  int version) {
		return put(namespace, key, value, version, 0);
	}

	public ResultCode put(int namespace, Serializable key, Serializable value,
						  int version, int expireTime) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}

		if (expireTime < 0){
			return ResultCode.INVALIDARG;
		}
		RequestPutPacket packet = new RequestPutPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setData(value);
		packet.setVersion((short) version);
		packet.setExpired(expireTime);

		int ec = packet.encode();

		if (ec == TairConstant.KEYTOLARGE) {
			return ResultCode.KEYTOLARGE;
		} else if (ec == TairConstant.VALUETOLARGE) {
			return ResultCode.VALUETOLARGE;
		} else if (ec == TairConstant.SERIALIZEERROR) {
			return ResultCode.SERIALIZEERROR;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;

			if (log.isDebugEnabled()) {
				log.debug("get return packet: " + returnPacket + ", code="
						  + r.getCode() + ", msg=" + r.getMsg());
			}

			rc = ResultCode.valueOf(r.getCode());

			configServer.checkConfigVersion(r.getConfigVersion());
		}
		return rc;
	}

	// @nayan. lock/unlock/mlock/munlock to lock a key
	public ResultCode lock(int namespace, Serializable key) {
		return doLock(namespace, key, RequestLockPacket.LOCK_VALUE, "lock");
	}

	public ResultCode unlock(int namespace, Serializable key) {
		return doLock(namespace, key, RequestLockPacket.UNLOCK_VALUE, "unlock");
	}

	public Result<List<Object>> mlock(int namespace, List<? extends Object> keys) {
		return doMLock(namespace, keys, RequestLockPacket.LOCK_VALUE, "mlock", null);
	}

	public Result<List<Object>> mlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap) {
		return doMLock(namespace, keys, RequestLockPacket.LOCK_VALUE, "mlock", failKeysMap);
	}

	public Result<List<Object>> munlock(int namespace, List<? extends Object> keys) {
		return doMLock(namespace, keys, RequestLockPacket.UNLOCK_VALUE, "munlock", null);
	}

	public Result<List<Object>> munlock(int namespace, List<? extends Object> keys, Map<Object, ResultCode> failKeysMap) {
		return doMLock(namespace, keys, RequestLockPacket.UNLOCK_VALUE, "munlock", failKeysMap);
	}

	private ResultCode doLock(int namespace, Serializable key, int lockType, String descStr) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		RequestLockPacket packet = new RequestLockPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setLockType(lockType);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			rc = ResultCode.valueOf(((ReturnPacket) returnPacket).getCode());
		} else {
			if (failCounter.incrementAndGet() > 100) {
				configServer.checkConfigVersion(0);
				failCounter.set(0);
				log.warn("connection failed happened 100 times, sync configuration");
			}
		}
		return rc;
	}

	private Result<List<Object>> doMLock(int namespace, List<? extends Object> keys,
										 int lockType, String descStr,
										 Map<Object, ResultCode> failKeysMap) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<List<Object>>(ResultCode.NSERROR);
		}

		if (failKeysMap != null) {
			failKeysMap.clear();
		}
		List<Object> sucList = null;
		Set<Object> uniqueKeys = new HashSet<Object>();

		for (Object key : keys) {
			uniqueKeys.add(key);
		}

		int sucRespSize = 0;
		int maxConfigVersion = 0;
		int retCode;

		// just one by one
		for (Object key : uniqueKeys) {
			RequestLockPacket packet = new RequestLockPacket(transcoder);
			packet.setNamespace((short) namespace);
			packet.setLockType(lockType);
			packet.setKey(key);
			int ec = packet.encode();

			if (ec == 1) {
				log.error("key too larget");
				if (failKeysMap != null) {
					failKeysMap.put(key, ResultCode.KEYTOLARGE);
				}
				continue;
			}

			BasePacket returnPacket = sendRequest(key, packet);

			if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
				if (((ReturnPacket)returnPacket).getConfigVersion() > maxConfigVersion) {
					maxConfigVersion = ((ReturnPacket) returnPacket).getConfigVersion();
				}
				retCode = ((ReturnPacket) returnPacket).getCode();
			} else {
				retCode = ResultCode.CONNERROR.getCode();
				log.warn("receive wrong packet type: " + returnPacket);
				if (failCounter.incrementAndGet() > 100) {
					configServer.checkConfigVersion(0);
					failCounter.set(0);
					log.warn("connection failed happened 100 times, sync configuration");
				}
			}

			if (retCode == ResultCode.SUCCESS.getCode()) {
				if (sucList == null) {
					sucList = new ArrayList<Object>();
				}
				sucList.add(key);
				++sucRespSize;
			} else if (failKeysMap != null) {
				failKeysMap.put(key, ResultCode.valueOf(retCode));
			}
		}			
		configServer.checkConfigVersion(maxConfigVersion);

		ResultCode rc = null;
		if (sucRespSize == uniqueKeys.size()) {
			rc = ResultCode.SUCCESS;
		} else {
			if (log.isDebugEnabled()) {
				log.error(descStr + "partly success: request key size: " + uniqueKeys.size()
						  + ", fail " + (uniqueKeys.size() - sucRespSize));
			}
			rc = ResultCode.PARTSUCC;
		}

		return new Result<List<Object>>(rc, sucList);
	}


	// items impl
	public ResultCode addItems(int namespace, Serializable key,
			List<? extends Object> items, int maxCount, int version,
			int expireTime) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		
		if (maxCount <= 0 || expireTime < 0) {
			return ResultCode.INVALIDARG;
		}

		RequestAddItemsPacket packet = new RequestAddItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.setKey(key);
		packet.setData(items);
		packet.setVersion((short) version);
		packet.setExpired(expireTime);
		packet.setMaxCount(maxCount);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		} else if (ec == 2) {
			return ResultCode.VALUETOLARGE;
		} else if (ec == 3) {
			return ResultCode.SERIALIZEERROR;
		}
		
		

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);
		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;

			if (log.isDebugEnabled()) {
				log.debug("get return packet: " + returnPacket + ", code="
						+ r.getCode() + ", msg=" + r.getMsg());
			}

			if (r.getCode() == 0) {
				rc = ResultCode.SUCCESS;
			} else if (r.getCode() == 2) {
				rc = ResultCode.VERERROR;
			} else {
				rc = ResultCode.SERVERERROR;
			}

			configServer.checkConfigVersion(r.getConfigVersion());
		}
		return rc;
	}

	public Result<DataEntry> getAndRemove(int namespace,
			Serializable key, int offset, int count) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<DataEntry>(ResultCode.NSERROR);
		}
		
		if (count <= 0) {
			return new Result<DataEntry>(ResultCode.INVALIDARG);
		}
		RequestGetAndRemoveItemsPacket packet = new RequestGetAndRemoveItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setCount(count);
		packet.setOffset(offset);
		packet.setType(Json.ELEMENT_TYPE_INVALID);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<DataEntry>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<DataEntry>(ResultCode.VALUETOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		DataEntry entry = null;
		if ((returnPacket != null) && returnPacket instanceof ResponseGetItemsPacket) {
			ResponseGetItemsPacket r = (ResponseGetItemsPacket) returnPacket;
			
			
			List<DataEntry> entryList = r.getEntryList();

			rc = ResultCode.valueOf(r.getResultCode());
			
			if (rc.isSuccess() && entryList.size() > 0) {
				entry = entryList.get(0);
				try {
					entry.setValue(Json.deSerialize((byte[]) entry.getValue()));
				} catch (Throwable e1) {
					log.error("ITEM SERIALIZEERROR", e1);
					rc = ResultCode.SERIALIZEERROR;
				}
			}

			configServer.checkConfigVersion(r.getConfigVersion());
		}
		return new Result<DataEntry>(rc, entry);
	}

	public Result<DataEntry> getItems(int namespace,
			Serializable key, int offset, int count) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<DataEntry>(ResultCode.NSERROR);
		}
		
		if (count <= 0) {
			return new Result<DataEntry>(ResultCode.INVALIDARG);
		}
		RequestGetItemsPacket packet = new RequestGetItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setCount(count);
		packet.setOffset(offset);
		packet.setType(Json.ELEMENT_TYPE_INVALID);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<DataEntry>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<DataEntry>(ResultCode.VALUETOLARGE);
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		DataEntry entry = null;
		if ((returnPacket != null) && returnPacket instanceof ResponseGetItemsPacket) {
			ResponseGetItemsPacket r = (ResponseGetItemsPacket) returnPacket;
			
			
			List<DataEntry> entryList = r.getEntryList();

			rc = ResultCode.valueOf(r.getResultCode());

			if (rc.isSuccess() && entryList.size() > 0) {
				entry = entryList.get(0);
				try {
					entry.setValue(Json.deSerialize((byte[]) entry.getValue()));
				} catch (Throwable e1) {
					log.error("ITEM SERIALIZEERROR", e1);
					rc = ResultCode.SERIALIZEERROR;
				}
			}
			
			configServer.checkConfigVersion(r.getConfigVersion());
		}	
		return new Result<DataEntry>(rc, entry);
	}

	public ResultCode removeItems(int namespace, Serializable key, int offset,
			int count) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return ResultCode.NSERROR;
		}
		
		if (count <= 0) {
			return ResultCode.INVALIDARG;
		}
		RequestRemoveItemsPacket packet = new RequestRemoveItemsPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);
		packet.setCount(count);
		packet.setOffset(offset);

		int ec = packet.encode();

		if (ec == 1) {
			return ResultCode.KEYTOLARGE;
		} else if (ec == 2) {
			return ResultCode.VALUETOLARGE;
		}

		ResultCode rc = ResultCode.CONNERROR;
		BasePacket returnPacket = sendRequest(key, packet);

		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;
			
			rc = ResultCode.valueOf(r.getCode());

			configServer.checkConfigVersion(r.getConfigVersion());
		}
		return rc;
	}
	
	public Result<Integer> getItemCount(int namespace, Serializable key) {
		if ((namespace < 0) || (namespace > TairConstant.NAMESPACE_MAX)) {
			return new Result<Integer>(ResultCode.NSERROR);
		}
		RequestGetItemsCountPacket packet = new RequestGetItemsCountPacket(transcoder);

		packet.setNamespace((short) namespace);
		packet.addKey(key);

		int ec = packet.encode();

		if (ec == 1) {
			return new Result<Integer>(ResultCode.KEYTOLARGE);
		} else if (ec == 2) {
			return new Result<Integer>(ResultCode.VALUETOLARGE);
		}

		ResultCode rc = ResultCode.SUCCESS;
		BasePacket returnPacket = sendRequest(key, packet);
		
		int count = 0;
		if ((returnPacket != null) && returnPacket instanceof ReturnPacket) {
			ReturnPacket r = (ReturnPacket) returnPacket;
			
			count = ((ReturnPacket) returnPacket).getCode();
			if (count < 0)
				rc = ResultCode.valueOf(count);

			configServer.checkConfigVersion(r.getConfigVersion());
		}

		return new Result<Integer>(rc, count);
	}
   
	public Map<String,String> getStat(int qtype, String groupName, long serverId){
		Map<String, String> temp = configServer.retrieveStat(qtype, groupName, serverId);
		return temp;
	}
	
	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getCompressionThreshold() {
		return compressionThreshold;
	}

	public void setCompressionThreshold(int compressionThreshold) {
		if (compressionThreshold <= TairConstant.TAIR_KEY_MAX_LENTH) {
			log.warn("compress threshold can not bigger than max key length["
					+ TairConstant.TAIR_KEY_MAX_LENTH + "], you provided:["
					+ compressionThreshold + "]");
		} else {
			this.compressionThreshold = compressionThreshold;
		}
	}

	public List<String> getConfigServerList() {
		return configServerList;
	}

	public void setConfigServerList(List<String> configServerList) {
		this.configServerList = configServerList;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getMaxWaitThread() {
		return maxWaitThread;
	}

	public void setMaxWaitThread(int maxWaitThread) {
		this.maxWaitThread = maxWaitThread;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String toString() {
		return name + " " + getVersion();
	}
	
	public ConfigServer getConfigServer() {
		return configServer;
	}
}
