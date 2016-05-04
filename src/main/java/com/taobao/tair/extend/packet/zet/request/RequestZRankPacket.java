package com.taobao.tair.extend.packet.zet.request;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class RequestZRankPacket extends BasePacket {
	private static final int HEADER_LEN = 1 + 2 + 4 + 4;
	
	private short namespace = 0;
	private Object key = null;
	private Object value = null;
	
	
	public RequestZRankPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_ZRANK_PACKET;
	}

	public RequestZRankPacket() {
		super();
		pcode = TairConstant.TAIR_REQ_ZRANK_PACKET;
	}

	public int encode() {
		byte[] keybytes = null;
		byte[] valuebytes = null;
		if(key == null || value == null) {
			return TairConstant.SERIALIZEERROR;
		}
		try {
			keybytes = transcoder.encode(key);
			valuebytes = transcoder.encode(value);
		} catch(Throwable e) {
			return TairConstant.SERIALIZEERROR;
		}
		if (keybytes == null || valuebytes == null) {
			return TairConstant.SERIALIZEERROR;
		}
		
		if (keybytes.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return TairConstant.KEYTOLARGE;
		}
	    if (valuebytes.length >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
			return TairConstant.VALUETOLARGE;
		}
		
		writePacketBegin(HEADER_LEN + keybytes.length + valuebytes.length);
		byteBuffer.put((byte)0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		byteBuffer.putInt(valuebytes.length);
		byteBuffer.put(valuebytes);
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
	
	public void setvalue(Object value) {
		this.value = value;
	}
	public Object getvalue() {
		return this.value;
	}
}
