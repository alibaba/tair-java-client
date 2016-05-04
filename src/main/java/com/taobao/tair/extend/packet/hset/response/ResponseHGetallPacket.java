package com.taobao.tair.extend.packet.hset.response;

import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.Map;

import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class ResponseHGetallPacket extends BasePacket {

	private int configVersion = 0;
	private short version = 0;
	private int resultCode = 0;
	private Map<Object, Object> map = new HashMap<Object, Object>();
	
	public ResponseHGetallPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_HGETALL_PACKET;
	}

	public int encode() {
		throw new UnsupportedOperationException();
	}
	
	public boolean decode() {
		Object ofield = null;
		Object ovalue = null;
    	try {
	    	configVersion 	= byteBuffer.getInt();
	    	version 		= byteBuffer.getShort();
	    	resultCode 		= byteBuffer.getInt();
	        int count 		= byteBuffer.getInt();
	        for (int i = 0; i < count; i += 2) {
	        	ofield = null;
	        	ovalue = null;
	        	
	        	int fieldsize = byteBuffer.getInt();
	        	if (fieldsize > 0) {
					try {
						ofield = transcoder.decode(byteBuffer.array(),
								byteBuffer.position(), fieldsize);
					} catch (Throwable e) {
						resultCode =  ResultCode.SERIALIZEERROR.getCode();
						return false;
					}
					if (ofield == null) {
						resultCode = ResultCode.SERIALIZEERROR.getCode();
						return false;
					}
					byteBuffer.position(byteBuffer.position() + fieldsize);
				}
	        	
	        	int valuesize = byteBuffer.getInt();
	        	if (valuesize > 0) {
	        		try {
						ovalue = transcoder.decode(byteBuffer.array(), 
							byteBuffer.position(), valuesize);
					} catch (Throwable e) {
						resultCode =  ResultCode.SERIALIZEERROR.getCode();
						return false;
					}
					if (ovalue == null) {
						resultCode = ResultCode.SERIALIZEERROR.getCode();
						return false;
					}
					byteBuffer.position(byteBuffer.position() + valuesize);
	        	}
	        	
	        	if (ofield != null && ovalue != null) {
	        		map.put(ofield, ovalue);
	        	}
	        }
    	} catch (BufferUnderflowException e) {
    		resultCode =  ResultCode.SERIALIZEERROR.getCode();
			return false;
    	}
		
		return true;
	}
	
	public void setConfigVersion(int configVersion) {
		this.configVersion = configVersion;
	}
	public int getConfigVersion() {
		return this.configVersion;
	}
	
	public short getVersion() {
		return version;
	}
	public void setVersion(short version) {
		this.version = version;
	}
	
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public int getResultCode() {
		return this.resultCode;
	}
	
	public Map<Object, Object> getValues() {
		return map;
	}
	public void setValues(Map<Object, Object> map) {
		if (map == null) {
			return;
		}
		this.map = map;
	}
}
