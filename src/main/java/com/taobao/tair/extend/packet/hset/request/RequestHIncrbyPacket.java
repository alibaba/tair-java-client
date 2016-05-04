package com.taobao.tair.extend.packet.hset.request;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class RequestHIncrbyPacket extends BasePacket {

	private final static int HEADER_LEN = 1 + 2 + 2 + 4 + 4 + 4;
	
	private short namespace = 0;
	private short version = 0;
	private int expire = 0;
	private Object key = null;
	private Object field = null;
	private long addvalue = 0;
	
	public RequestHIncrbyPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_HINCRBY_PACKET;
	}

	public RequestHIncrbyPacket() {
		super();
		pcode = TairConstant.TAIR_REQ_HINCRBY_PACKET;
	}

	public int encode() {
		byte[] keybytes = null;
		byte[] fieldbytes = null;
		if(key == null || field == null) {
			return TairConstant.SERIALIZEERROR;
		}
		try {
			keybytes = transcoder.encode(key);
			fieldbytes = transcoder.encode(field);
		} catch (Throwable e) {
			return TairConstant.SERIALIZEERROR;
		}
		if (keybytes == null || fieldbytes == null) {
			return TairConstant.SERIALIZEERROR;
		}
		
		if (keybytes.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return TairConstant.KEYTOLARGE;
		}
	    if (fieldbytes.length >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
			return TairConstant.VALUETOLARGE;
		}
		
		writePacketBegin(HEADER_LEN + keybytes.length + fieldbytes.length);
		byteBuffer.put((byte)0);
		byteBuffer.putShort(namespace);
		byteBuffer.putShort(version);
		byteBuffer.putInt(expire);
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		byteBuffer.putInt(fieldbytes.length);
		byteBuffer.put(fieldbytes);
		byteBuffer.putLong(addvalue);
		writePacketEnd();
		
		return 0;
	}
	
	public boolean decode() {
		throw new UnsupportedOperationException();
	}
	
	public void setNamespace(short namespace) {
		this.namespace = namespace;
	}
	
	public void setKey(Object key) {
		this.key = key;
	}
	
	public void setField(Object field) {
		this.field = field;
	}
	
	public void setAddValue(long addvalue) {
		this.addvalue = addvalue;
	}
	
	public void setVersion(short version) {
		this.version = version;
	}
	public short getVersion() {
		return this.version;
	}
	
	public void setExpire(int expire) {
		this.expire = expire;
	}
	public int getExpire() {
		return this.expire;
	}
}
