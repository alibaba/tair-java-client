package com.taobao.tair.extend.packet.zet.request;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.helper.BytesHelper;
import com.taobao.tair.packet.BasePacket;

public class RequestZIncrbyPacket extends BasePacket {

	private final static int HEADER_LEN = 1 + 2 + 2 + 4 + 4 + 4;
	
	private short namespace = 0;
	private short version = 0;
	private int expire = 0;
	private Object key = null;
	private Object value = null;
	private double addvalue = 0;
	
	public RequestZIncrbyPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_ZINCRBY_PACKET;
	}

	public RequestZIncrbyPacket() {
		super();
		pcode = TairConstant.TAIR_REQ_ZINCRBY_PACKET;
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
		} catch (Throwable e) {
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
		byteBuffer.putShort(version);
		byteBuffer.putInt(expire);
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		byteBuffer.putInt(valuebytes.length);
		byteBuffer.put(valuebytes);
		byteBuffer.put(BytesHelper.DoubleToBytes_With_Little_Endian(addvalue));
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
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public void setAddValue(double addvalue) {
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
