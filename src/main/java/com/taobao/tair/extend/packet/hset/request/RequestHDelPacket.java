package com.taobao.tair.extend.packet.hset.request;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class RequestHDelPacket extends BasePacket {

	private static final int HEARDER_LEN = 1 + 2 + 2 + 4 + 4 + 4;
	
	private short namespace = 0;
	private short version = 0;
	private int expire = 0;
	private Object key = null;
	private Object field = null;
	
	public RequestHDelPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_HDEL_PACKET;
	}

	public RequestHDelPacket() {
		super();
		pcode = TairConstant.TAIR_REQ_HDEL_PACKET;
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
		if(keybytes == null || fieldbytes == null) {
			return TairConstant.SERIALIZEERROR;
		}
		
		if (keybytes.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return TairConstant.KEYTOLARGE;
		}
		if (fieldbytes.length >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
			return TairConstant.VALUETOLARGE;
		}
		writePacketBegin(keybytes.length + fieldbytes.length + HEARDER_LEN);
		byteBuffer.put((byte)0);         //1
		byteBuffer.putShort(namespace);  //2
		byteBuffer.putShort(version);    //2
		byteBuffer.putInt(expire);
		byteBuffer.putInt(keybytes.length);  //4
		byteBuffer.put(keybytes);
		byteBuffer.putInt(fieldbytes.length); //4
		byteBuffer.put(fieldbytes);
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
	
	public void setField(Object field) {
		this.field = field;
	}
	public Object getField() {
		return this.field;
	}
	
	public void setVersion(short version) {
		this.version = version;
	}
	public int getVersion() {
		return this.version;
	}
	
	public void setExpire(int expire) {
		this.expire = expire;
	}
	public int getExpire() {
		return this.expire;
	}
}
