package com.taobao.tair.extend.packet.set.response;

import java.nio.BufferUnderflowException;
import java.util.HashSet;
import java.util.Set;

import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class ResponseSMembersPacket extends BasePacket {

	private int configVersion = 0;
	private short version = 0;
	private int resultCode = 0;
	private Set<Object> set = new HashSet<Object>();
	
	public ResponseSMembersPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_SMEMBERS_PACKET;
	}

	public int encode() {
		throw new UnsupportedOperationException();
	}
	
	public boolean decode() {
		Object ovalue = null;
    	try {
	    	configVersion 	= byteBuffer.getInt();
	    	version 		= byteBuffer.getShort();
	    	resultCode 		= byteBuffer.getInt();
	        int count 		= byteBuffer.getInt();
	        for (int i = 0; i < count; ++i) {
	        	ovalue = null;
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
	        	
	        	set.add(ovalue);
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
	
	public Set<Object> getValues() {
		return set;
	}
	public void setData(Set<Object> set) {
		if (set == null) {
			return;
		}
		this.set = set;
	}
}
