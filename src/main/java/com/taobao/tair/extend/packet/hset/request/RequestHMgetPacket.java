package com.taobao.tair.extend.packet.hset.request;

import java.util.ArrayList;
import java.util.List;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class RequestHMgetPacket extends BasePacket {
	
	private final static int HEADER_LEN = 1 + 2 + 4 + 4;
	
	private short namespace = 0;
	private Object key = null;
	private List<Object> fields = new ArrayList<Object>();
	
	private List<byte[]> fieldsbytes = new ArrayList<byte[]>();
	
	public RequestHMgetPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_HMGET_PACKET;
	}

	public RequestHMgetPacket() {
		super();
		pcode = TairConstant.TAIR_REQ_HMGET_PACKET;
	}

	public int encode() {
		byte[] keybytes = null;
		byte[] fieldbytes = null;
		if(key == null || fields.isEmpty()) {
			return TairConstant.SERIALIZEERROR;
		}
		try {
			keybytes = transcoder.encode(key);
		} catch (Throwable e) {
			return TairConstant.SERIALIZEERROR;
		}
		if(keybytes == null) {
			return TairConstant.SERIALIZEERROR;
		}
		if(keybytes.length >= TairConstant.TAIR_KEY_MAX_LENTH) {
			return TairConstant.KEYTOLARGE;
		}
		
		int fieldslen = 0;
		for(Object ofield : fields) {
			if(ofield == null) {
				return TairConstant.SERIALIZEERROR;
			}
			try {
				fieldbytes = transcoder.encode(ofield);
			} catch (Throwable e) {
				return TairConstant.SERIALIZEERROR;
			}
			if (fieldbytes == null) {
				return TairConstant.SERIALIZEERROR;
			}
			if (fieldbytes.length >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
				return TairConstant.VALUETOLARGE;
			}
			fieldsbytes.add(fieldbytes);
			fieldslen += fieldbytes.length;
		}
		
		writePacketBegin(HEADER_LEN + keybytes.length + fieldslen + 4*fields.size());
		byteBuffer.put((byte)0);
		byteBuffer.putShort(namespace);
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		byteBuffer.putInt(fields.size());
		for(byte[] tfieldbytes : fieldsbytes) {
			byteBuffer.putInt(tfieldbytes.length);
			byteBuffer.put(tfieldbytes);
		}
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
	
	public void addField(Object field) {
		fields.add(field);
	}
	public Object getField(int index) {
		if(index < 0 || index >= fields.size()) {
			return null;
		}
		return fields.get(index);
	}
}
