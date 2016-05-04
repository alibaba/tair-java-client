/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.comm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.common.IoSession;

import com.taobao.tair.etc.TairClientException;
import com.taobao.tair.etc.TairUtil;
import com.taobao.tair.packet.BasePacket;
import com.taobao.tair.packet.PacketStreamer;
import com.taobao.tair.packet.RequestCommandCollection;


public class MultiSender {
	
	private TairClientFactory clientFactory;
	
    private PacketStreamer   packetStreamer = null;

    public MultiSender(TairClientFactory factory, PacketStreamer packetStreamer) {
        this.packetStreamer = packetStreamer;
        this.clientFactory = factory;
    }

   
    public boolean sendRequest(RequestCommandCollection rcList, int timeout) {
        Map<Long, BasePacket> map       = rcList.getRequestCommandMap();
        MultiReceiveListener               listener  = new MultiReceiveListener(rcList.getResultList());
        int                                sendCount = 0;

        for (Long addr : map.keySet()) {
            
            TairClient client = null;
            
            try {
            	client = clientFactory.get(TairUtil.idToAddress(addr), timeout, packetStreamer);
			} catch (TairClientException e) {				
			}

            if (client == null) {
                continue;
            }

            
           client.invokeAsync(map.get(addr), timeout, listener);
           sendCount ++;
        }

        listener.await(sendCount, timeout);

        return (sendCount == listener.doneCount);
    }

    public class MultiReceiveListener implements ResponseListener {
        private List<BasePacket> resultList = null;
        private ReentrantLock    lock       = null;
        private Condition        cond       = null;
        private int              doneCount  = 0;

        public MultiReceiveListener(List<BasePacket> resultList) {
            this.resultList = resultList;
            this.lock       = new ReentrantLock();
            this.cond       = this.lock.newCondition();
        }
        
		public void responseReceived(Object response) {
			lock.lock();

			try {
				resultList.add((BasePacket) response);
				this.doneCount++;
				cond.signal();
			} finally {
				lock.unlock();
			}
		}


		public boolean await(int count, int timeout) {
            long t = TimeUnit.MILLISECONDS.toNanos(timeout);

            lock.lock();

            try {
                while (this.doneCount < count) {
                    if ((t = cond.awaitNanos(t)) <= 0) {
                        return false;
                    }
                }
            } catch (InterruptedException e) {
                return false;
            } finally {
                lock.unlock();
            }
    
            return true;
        }

		public void exceptionCaught(IoSession session,
				TairClientException exception) {
		}
    }
}
