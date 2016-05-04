/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.RequestPacketInterface;
import com.taobao.tair.extend.packet.ResponsePacketInterface;

public abstract class BasePacket 
		implements RequestPacketInterface, ResponsePacketInterface {
    protected Exception          exception    = null;
    protected ByteBuffer         byteBuffer   = null;
    protected int                chid         = 0;
    protected int                pcode        = 0;
    protected int                len          = 0;
    
    protected int                bodyLen          = 0;
    
    private BasePacket           returnPacket = null;
    private static AtomicInteger globalChid   = new AtomicInteger(0);
    protected Transcoder         transcoder   = null;
    private long                 startTime    = 0;
    
    // lock & contition
    private ReentrantLock lock;
    private Condition cond;
    
    
    /**
     * 运程服务器地址
     * @author xiaodu
     * 
     */
    private SocketAddress remoteAddress;    
    public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
	public BasePacket() {
	}
	
	public void setTranscode(Transcoder transcoder) {
		this.transcoder = transcoder;
	}

    public BasePacket(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    public ByteBuffer getByteBuffer() {
        if (byteBuffer == null) {
            encode();
        }

        return byteBuffer;
    }

    protected void writeString(String str) {
        if (str == null) {
            byteBuffer.putInt(0);
        } else {
            byte[] b = str.getBytes();

            byteBuffer.putInt(b.length + 1);
            byteBuffer.put(b);
            byteBuffer.put((byte) 0);
        }
    }

    protected String readString() {
        int len = byteBuffer.getInt();

        if (len <= 1) {
            return "";
        } else {
            byte[] b = new byte[len];

            byteBuffer.get(b);
            return new String(b, 0, len - 1);
        }
    }

    public int encode() {
        return 0;
    }

    public boolean decode() {
        if ((byteBuffer == null) || (byteBuffer.remaining() < len)) {
            return false;
        }

        byte[] tmp = new byte[len];

        byteBuffer.get(tmp);
        return true;
    }

    protected void writePacketBegin(int capacity) {
        chid = globalChid.incrementAndGet();

        // packet header
        byteBuffer = ByteBuffer.allocate(capacity + 256);
        byteBuffer.putInt(TairConstant.TAIR_PACKET_FLAG); // packet flag
        byteBuffer.putInt(chid); // channel id
        byteBuffer.putInt(pcode); // packet code
        byteBuffer.putInt(0); // body len
    }

    protected void writePacketEnd() {
        int len = byteBuffer.position() - TairConstant.TAIR_PACKET_HEADER_SIZE;
        this.bodyLen = len;
        byteBuffer.putInt(TairConstant.TAIR_PACKET_HEADER_BLPOS, len);
    }

    public BasePacket getReturnPacket(int timeout) {
    	if(lock == null)
    		return returnPacket;
    	
    	lock.lock();
		try {
			while (returnPacket == null)
				cond.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception e) {

		} finally {
			lock.unlock();
		}
		return returnPacket;
    }

    public void setReturnPacket(BasePacket returnPacket) {
    	if (lock == null) {
			this.returnPacket = returnPacket;
			return;
		}
		lock.lock();
		this.returnPacket = returnPacket;
		try {
			cond.signal();
		} finally {
			lock.unlock();
		}
	}

    public void setByteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.get(this.byteBuffer.array());
    }

    public int getPcode() {
        return pcode;
    }

    public void setPcode(int pcode) {
        this.pcode = pcode;
    }

    public void setChid(int chid) {
        this.chid = chid;
    }

    public int getChid() {
    	if (chid == 0)
    		encode();
        return chid;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "basepacket: chid=" + chid + ", pcode=" + pcode + ", len=" + len;
    }

    /**
     * 
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }
    
    public void initCondition() {
    	lock = new ReentrantLock();
    	cond = lock.newCondition();
    }
    
    
    

    public int getBodyLen() {
		return bodyLen;
	}

	/**
     * 
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
    
    protected void removeMetas() {
		byteBuffer.get(); // isMerged
		byteBuffer.getInt(); // area
		byteBuffer.getShort(); // serverFlag		
	}
    
    protected void fillMetas() {
    	byte[] data = new byte[7];
		byteBuffer.put(data);
	}

	public int getConfigVersion() {
		// Do nothing
		return 0;
	}

	public void setNamespace(short namespace) {
		// Do nothing
	}

	public void setVersion(short version) {
		// Do nothing
	}

	public void setExpire(int expire) {
		// Do nothing
	}

	public void setKey(Object key) {
		// Do nothing
	}
}
