package com.taobao.tair.extend.packet.zet.response;

import java.nio.BufferUnderflowException;

import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.helper.BytesHelper;
import com.taobao.tair.packet.BasePacket;

public class ResponseZScorePacket extends BasePacket {

	private int configVersion = 0;
//	private short version = 0;
	private int resultCode = 0;
	private double value = 0;
	
	public ResponseZScorePacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_ZSCORE_PACKET;
	}


	public int encode() {
		throw new UnsupportedOperationException();
	}
	
	public boolean decode() {
    	try {
	    	configVersion 	= byteBuffer.getInt();
//	    	version 		= byteBuffer.getShort();
			resultCode = byteBuffer.getInt();
			value = BytesHelper.LongToDouble_With_Little_Endian(byteBuffer.getLong());
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
	
//	public void setVersion(short version) {
//		this.version = version;
//	}
//	public short getVersion() {
//		return this.version;
//	}
	
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public int getResultCode() {
		return this.resultCode;
	}
	
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
}
