package com.taobao.tair.extend.packet.zet.request;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class RequestZRangePacket extends BasePacket {
	
	private final static int HEADER_LEN = 1 + 2 + 4 + 4 + 4;
	
	private short namespace = 0;
	private Object key = null;
	private int start = 0;
	private int end = 0;
	
	public RequestZRangePacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_ZRANGE_PACKET;
	}

	public RequestZRangePacket() {
		super();
		pcode = TairConstant.TAIR_REQ_ZRANGE_PACKET;
	}

	public int encode() {
		byte[] keybytes = null;
		if (key == null) {
			return TairConstant.SERIALIZEERROR;
		}
		try {
			keybytes = transcoder.encode(key);
		} catch (Throwable e) {
			return TairConstant.SERIALIZEERROR;
		}
		if (keybytes == null) {
			return TairConstant.SERIALIZEERROR;
		}
		
		if(keybytes.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return TairConstant.KEYTOLARGE;
		}
		
		writePacketBegin(HEADER_LEN + keybytes.length);
		byteBuffer.put((byte)0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(start);
		byteBuffer.putInt(end);
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		writePacketEnd();
		
		return 0;
	}
	
	public boolean decode() {
		throw new UnsupportedOperationException();
	}
	
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}
	public short getNamespace() {
		return this.namespace;
	}
	
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getKey() {
		return this.key;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	public int getStart() {
		return this.start;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	public int getEnd() {
		return this.end;
	}
}
