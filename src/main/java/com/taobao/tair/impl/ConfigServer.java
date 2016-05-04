/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoSession;

import com.taobao.tair.comm.ResponseListener;
import com.taobao.tair.comm.TairClient;
import com.taobao.tair.comm.TairClientFactory;
import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.packet.BasePacket;
import com.taobao.tair.packet.PacketStreamer;
import com.taobao.tair.packet.RequestGetGroupPacket;
import com.taobao.tair.packet.RequestQueryInfoPacket;
import com.taobao.tair.packet.ResponseGetGroupPacket;
import com.taobao.tair.packet.ResponseQueryInfoPacket;

public class ConfigServer implements ResponseListener {
  private static final Log log = LogFactory.getLog(ConfigServer.class);
  private static final int MURMURHASH_M = 0x5bd1e995;
  private String groupName = null;
  private int configVersion = 0;
  private AtomicLong retrieveLastTime = new AtomicLong(0);

  private List<String> configServerList = new ArrayList<String>();

  private List<Long> serverList;
  private PacketStreamer pstream;

  private int bucketCount = 0;
  private int copyCount = 0;

  private Set<Long> aliveNodes;
  
  private int masterFailCount;
  
  private final int MAX_MASTER_FAILCOUNT = 3;
  
  private TairClientFactory factory;

  public ConfigServer(TairClientFactory factory, String groupName, List<String> configServerList,
      PacketStreamer pstream) {
    this.groupName = groupName;
    this.pstream = pstream;
    this.masterFailCount = 0;
    this.factory = factory;
    
    for (String host : configServerList)
      this.configServerList.add(host.trim());
  }
  
  protected void resetConfigVersion() {
	  this.configVersion = 0;
  }
  
  protected int findServerIdx(byte[] keyByte) {
    long hash = murMurHash(keyByte); // cast to int is safe
    log.debug("hashcode: " + hash + ", bucket count: " + bucketCount);
    if ((serverList != null) && (serverList.size() > 0))
        return (int)(hash %= bucketCount);
    return 0;
  }

  public long getServer(byte[] keyByte, boolean isRead) {
	if (serverList == null || serverList.size() == 0) {
		log.error("server list is empty");
		return 0;
	}
	int serverIdx = findServerIdx(keyByte); 

	long serverIp = 0;
	serverIp = serverList.get(serverIdx);
	log.debug("oroginal target server: " + 
			TairUtil.idToAddress(serverIp) + 
			" alive server: " + aliveNodes);
	
	if (!aliveNodes.contains(serverIp)) {
		serverIp = 0;
		log.warn("master server " + TairUtil.idToAddress(serverIp) + " had down");
	}
	
	if (serverIp == 0 && isRead) {
		for (int i  = 1; i < copyCount; ++i) {
			int copyServerIdx = serverIdx + i * bucketCount;
			serverIp = serverList.get(copyServerIdx);
			log.debug("read operation try: " + TairUtil.idToAddress(serverIp));
			if (aliveNodes.contains(serverIp)) {
				
				break;
			} else {
				serverIp = 0;
			}
		}
		if (serverIp == 0) {
			log.warn("slave servers also"+ " had down");
		}
	}
    return serverIp;
  }
  
  public Map<String, String> grabGroupConfigMap() {
	  RequestGetGroupPacket request = new RequestGetGroupPacket(null);
	  request.setGroupName(groupName);
	  // force get group info, if client version equals server version, cs return empty
	  request.setConfigVersion(0);
	  
	  for (String addr  : configServerList) {
		  log.info("send request to " + addr);
		  ResponseGetGroupPacket response = null;
	      try {
	        TairClient client = factory.get(addr,
	            TairConstant.DEFAULT_CS_CONN_TIMEOUT, pstream);
	        response = (ResponseGetGroupPacket) client.invoke(request, TairConstant.DEFAULT_CS_TIMEOUT);
	      } catch (Exception e) {
	        log.error("get config failed from: " + addr, e);
	        continue;
	      }
	      if (response != null) {
	    	  return response.getConfigMap();
	      }
	  }
	  log.error("get config map null");
	  return null;
  }

  public boolean retrieveConfigure() {
    retrieveLastTime.set(System.currentTimeMillis());

    RequestGetGroupPacket packet = new RequestGetGroupPacket(null);

    packet.setGroupName(groupName);
    packet.setConfigVersion(configVersion);


    boolean initSuccess = false;

    for (int i = 0; i < configServerList.size(); i++) {
      String addr = configServerList.get(i);

      log.info("init configs from configserver: " + addr);

      BasePacket returnPacket = null;
      try {
        TairClient client = factory.get(addr,
            TairConstant.DEFAULT_CS_CONN_TIMEOUT, pstream);
        returnPacket = (BasePacket) client.invoke(packet,
            TairConstant.DEFAULT_CS_TIMEOUT);
      } catch (Exception e) {
        log.error("get config failed from: " + addr, e);
        continue;
      }

      if ((returnPacket != null)
          && returnPacket instanceof ResponseGetGroupPacket) {
        ResponseGetGroupPacket r = (ResponseGetGroupPacket) returnPacket;

        configVersion = r.getConfigVersion();
        bucketCount = r.getBucketCount();
        copyCount = r.getCopyCount();
        aliveNodes = r.getAliveNodes();

        if (aliveNodes == null || aliveNodes.isEmpty()) {
          log.error("fatal error, no datanode is alive");
          continue;
        }

        if (log.isInfoEnabled()) {
          for (Long id : aliveNodes) {
            log.info("alive datanode: " + TairUtil.idToAddress(id));
          }
        }

        if (bucketCount <= 0 || copyCount <= 0)
          throw new IllegalArgumentException("bucket count or copy count can not be 0");



        if ((r.getServerList() != null)
            && (r.getServerList().size() > 0)) {
          this.serverList = r.getServerList();
          if (log.isDebugEnabled()) {
            for (int idx = 0; idx < r.getServerList().size(); idx++) {
              log.debug("+++ " + idx + " => "
                  + TairUtil.idToAddress(r.getServerList().get(idx)));
            }
          }
          if ((this.serverList.size() % bucketCount) != 0) {
            log.error("server size % bucket number != 0, server size: "
                + this.serverList.size()
                + ", bucket number"
                + bucketCount
                + ", copyCount: " + copyCount);
          } else {
            log.warn("configuration inited with version: " + configVersion
                + ", bucket count: " + bucketCount + ", copyCount: "
                + copyCount);
            initSuccess = true;
            break;
          }
        } else {
          log.warn("server list from config server is null or size is 0");
        }

      } else {
        log.error("retrive from config server " + addr
            + " failed, result: " + returnPacket);
      }
    }

    return initSuccess;
  }


  public Map<String, String>retrieveStat(int qtype, String groupName, long serverId) {

    RequestQueryInfoPacket packet = new RequestQueryInfoPacket(null);

    packet.setGroupName(groupName);
    packet.setQtype(qtype);
    packet.setServerId(serverId);
    Map <String, String> statInfo = null;

    for (int i = 0; i < configServerList.size(); i++) {
      String addr = configServerList.get(i);

      BasePacket returnPacket = null;
      try {
        TairClient client = factory.get(addr,
            TairConstant.DEFAULT_CS_CONN_TIMEOUT, pstream);
        returnPacket = (BasePacket) client.invoke(packet,
            TairConstant.DEFAULT_CS_TIMEOUT);
      } catch (Exception e) {
        log.error("get stat failed " + addr, e);
        continue;
      }

      if ((returnPacket != null)
          && returnPacket instanceof ResponseQueryInfoPacket) {
        ResponseQueryInfoPacket r = (ResponseQueryInfoPacket) returnPacket;
        statInfo = r.getKv();

        break;
      } else {
        log.error("retrive stat from config server " + addr
            + " failed, result: " + returnPacket);
      }

    }

    return statInfo;
  }

  public void checkConfigVersion(int version) {
    if (version == configVersion) {
      return;
    }

    if (retrieveLastTime.get() > (System.currentTimeMillis() - 5000)) {
      log.debug("last check time is less than 5 seconds, need not sync");
      return;
    }

    retrieveLastTime.set(System.currentTimeMillis());

    RequestGetGroupPacket packet = new RequestGetGroupPacket(null);

    packet.setGroupName(groupName);
    packet.setConfigVersion(configVersion);

    for (int i = 0; i < configServerList.size(); i++) {
      if (i == 0 && masterFailCount > MAX_MASTER_FAILCOUNT) {
    	  masterFailCount = 0;
    	  continue;
      }
      
      String host = configServerList.get(i);
      try {
        TairClient client = factory.get(host,
            TairConstant.DEFAULT_CS_CONN_TIMEOUT, pstream);
        client.invokeAsync(packet, TairConstant.DEFAULT_CS_TIMEOUT, this);
        break;
      } catch (TairClientException e) {
        log.error("get client failed", e);
        continue;
      }
    }
  }

  public void responseReceived(Object packet) {

    if ((packet != null) && packet instanceof ResponseGetGroupPacket) {
      ResponseGetGroupPacket r = (ResponseGetGroupPacket) packet;
      r.decode();

      // always sync version
      //if (configVersion >= r.getConfigVersion() && (configVersion - r.getConfigVersion()) < 1000) {
      //	log.debug("sync configure returned, but local version is not older than configserver, local version: "
      //					+ configVersion + ", configserver version: " + r.getConfigVersion());
      //	return;
      //}

      log.warn("configuration synced, oldversion: " + configVersion
          + ", new verion: " + r.getConfigVersion());

      if (configVersion == r.getConfigVersion()) {
        return ;
      }

      aliveNodes = r.getAliveNodes();
      if (aliveNodes == null || aliveNodes.isEmpty()) {
        log.error("empty server table received, still use the old one");
      }

      configVersion = r.getConfigVersion();
      for (Long id : aliveNodes) {
        log.info("alive node: " + TairUtil.idToAddress(id));
      }

      if ((r.getServerList() != null) && (r.getServerList().size() > 0)) {
        this.serverList = r.getServerList();
        if (log.isDebugEnabled()) {
          for (int idx = 0; idx < r.getServerList().size(); idx++) {
            log.debug("+++ " + idx + " => "
                + TairUtil.idToAddress(r.getServerList().get(idx)));
          }
        }
      }else{
        log.error("configuration new verion: " + r.getConfigVersion()+" server list is empty!!!!!");
      }
    }

  }

  public void exceptionCaught(IoSession session, TairClientException exception) {
    log.error("do async request failed", exception);
    if (session.isConnected()) {
    	log.error("session closing");
    	session.close();
    }
    masterFailCount++;
  }

  public void close() {
	  
  }
  private long murMurHash(byte[] key) {
    int len = key.length;
    int h = 97 ^ len;
    int index = 0;

    while (len >= 4) {
      int k = (key[index] & 0xff) | ((key[index + 1] << 8) & 0xff00)
        | ((key[index + 2] << 16) & 0xff0000)
        | (key[index + 3] << 24);

      k *= MURMURHASH_M;
      k ^= (k >>> 24);
      k *= MURMURHASH_M;
      h *= MURMURHASH_M;
      h ^= k;
      index += 4;
      len -= 4;
    }

    switch (len) {
      case 3:
        h ^= (key[index + 2] << 16);

      case 2:
        h ^= (key[index + 1] << 8);

      case 1:
        h ^= key[index];
        h *= MURMURHASH_M;
    }

    h ^= (h >>> 13);
    h *= MURMURHASH_M;
    h ^= (h >>> 15);
    return ((long) h & 0xffffffffL);
  }
}
