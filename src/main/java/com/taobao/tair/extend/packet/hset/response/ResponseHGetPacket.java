package com.taobao.tair.extend.packet.hset.response;

import java.nio.BufferUnderflowException;

import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.packet.BasePacket;

public class ResponseHGetPacket extends BasePacket {

	private int configVersion = 0;
	private short version = 0;
	private int resultCode = 0;
	private Object value = null;
	
	public ResponseHGetPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_HGET_PACKET;
	}

	public int encode() {
		throw new UnsupportedOperationException();
	}
	
	public boolean decode() {
    	try {
	    	configVersion 	= byteBuffer.getInt();
	    	version 		= byteBuffer.getShort();
			resultCode = byteBuffer.getInt();

			int valuesize = byteBuffer.getInt();
			if (valuesize > 0) {
				try {
					value = transcoder.decode(byteBuffer.array(),
							byteBuffer.position(), valuesize);
				} catch (Throwable e) {
					resultCode = ResultCode.SERIALIZEERROR.getCode();
					return false;
				}
				if (value == null) {
					resultCode = ResultCode.SERIALIZEERROR.getCode();
					return false;
				}
				byteBuffer.position(byteBuffer.position() + valuesize);
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
	
	public void setVersion(short version) {
		this.version = version;
	}
	public short getVersion() {
		return this.version;
	}
	
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public int getResultCode() {
		return this.resultCode;
	}
	
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
