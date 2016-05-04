package com.taobao.tair.extend.packet.list.response;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.LeftOrRight;
import com.taobao.tair.extend.packet.ResponsePacketInterface;
import com.taobao.tair.packet.BasePacket;

public class ResponseLRPopPacket extends BasePacket 
		implements ResponsePacketInterface {
	
	private int configVersion;
	private short version;
	private List<Object> objs;
	private int resultCode;
	
    public ResponseLRPopPacket(Transcoder transcoder, LeftOrRight lr) {
    	super(transcoder);
    	this.objs = new ArrayList<Object>();
    	if (lr == LeftOrRight.IS_L)
    		this.pcode = TairConstant.TAIR_RESP_LPOP_PACKET;
    	else
    		this.pcode = TairConstant.TAIR_RESP_RPOP_PACKET;
	}

    public boolean decode() {
    	try {
	    	configVersion 	= byteBuffer.getInt();
	    	version 		= byteBuffer.getShort();
	    	resultCode 		= byteBuffer.getInt();
	        int count 		= byteBuffer.getInt();
	        for (int i = 0; i < count; ++i) {
	        	int size = byteBuffer.getInt();
	        	if (size > 0) {
	        		Object o = null;
					try {
						o = transcoder.decode(
							byteBuffer.array(), 
							byteBuffer.position(), size);
					} catch (Throwable e) {
						resultCode =  ResultCode.SERIALIZEERROR.getCode();
						return false;
					}
					if (o != null)
						objs.add(o);
					byteBuffer.position(byteBuffer.position() + size);
				}
	        }
    	} catch (BufferUnderflowException e) {
    		//TODO: log warning
    		resultCode =  ResultCode.SERIALIZEERROR.getCode();
			return false;
    	}
        return true;
    }

    

    public short getVersion() {
		return version;
	}

	public List<Object> getValues() {
		return objs;
	}

    public int getConfigVersion() {
        return configVersion;
    }

	public int getResultCode() {
		return resultCode;
	}
}
