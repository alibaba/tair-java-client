package com.taobao.tair.extend.packet.hset.request;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class RequestHGetPacket extends BasePacket {
	private static final int HEADER_LEN = 1 + 2 + 4 + 4;
	
	private short namespace = 0;
	private Object key = null;
	private Object field = null;
	
	
	public RequestHGetPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_HGET_PACKET;
	}

	public RequestHGetPacket() {
		super();
		pcode = TairConstant.TAIR_REQ_HGET_PACKET;
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
		} catch(Throwable e) {
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
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		byteBuffer.putInt(fieldbytes.length);
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
}
