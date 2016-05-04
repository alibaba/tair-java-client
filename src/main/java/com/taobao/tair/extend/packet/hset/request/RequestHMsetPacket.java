package com.taobao.tair.extend.packet.hset.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class RequestHMsetPacket extends BasePacket {
	private final static int HEADER_LEN = 1 + 2 + 2 + 4 + 4 + 4;
	
	private short namespace = 0;
	private short version = 0;
	private int expire = 0;
	private Object key = null;
	private Map<Object, Object> field_values 
		= new HashMap<Object, Object>();
	
	private List<byte[]> bytefieldvalues = new ArrayList<byte[]>();
	
	public RequestHMsetPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_REQ_HMSET_PACKET;
	}

	public RequestHMsetPacket() {
		super();
		pcode = TairConstant.TAIR_REQ_HMSET_PACKET;
	}

	public int encode() {
		byte[] keybytes = null;
		byte[] fieldbytes = null;
		byte[] valuebytes = null;
		if(key == null || field_values.isEmpty()) {
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
		
		int fieldvalueslen = 0;
		Set<Entry<Object, Object>> fvset = field_values.entrySet();
		for(Entry<Object, Object> fv : fvset) {
			try {
				fieldbytes = transcoder.encode(fv.getKey());
				if(fv.getValue() instanceof Long || fv.getValue() instanceof Integer
						|| fv.getValue() instanceof Short) {
					valuebytes = String.valueOf(fv.getValue()).getBytes();
				} else {
					valuebytes = transcoder.encode(fv.getValue());
				}
			} catch (Throwable e) {
				return TairConstant.SERIALIZEERROR;
			}
			if(fieldbytes == null || valuebytes == null) {
				return TairConstant.SERIALIZEERROR;
			}
			if(fieldbytes.length >= TairConstant.TAIR_VALUE_MAX_LENGTH ||
					valuebytes.length >= TairConstant.TAIR_VALUE_MAX_LENGTH) {
				return TairConstant.VALUETOLARGE;
			}
			bytefieldvalues.add(fieldbytes);
			fieldvalueslen += fieldbytes.length;
			bytefieldvalues.add(valuebytes);
			fieldvalueslen += valuebytes.length;
		}
		
		writePacketBegin(HEADER_LEN + keybytes.length + fieldvalueslen + 4*bytefieldvalues.size());
		byteBuffer.put((byte)0);
		byteBuffer.putShort(namespace);
		byteBuffer.putShort(version);
		byteBuffer.putInt(expire);
		byteBuffer.putInt(keybytes.length);
		byteBuffer.put(keybytes);
		byteBuffer.putInt(bytefieldvalues.size());
		for(byte[] tfieldbytes : bytefieldvalues) {
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
	
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getKey() {
		return this.key;
	}
	
	public void addFieldValue(Object field, Object value) {
		if (field == null || value == null) {
			return;
		}
		field_values.put(field, value);
	}
	public Object getValue(Object field) {
		if (field == null) {
			return null;
		}
		return field_values.get(field);
	}
	
	public void setValues(Map<Object, Object> field_values) {
		this.field_values = field_values;
	}
	public Map<Object, Object> getValues() {
		return field_values;
	}
}
