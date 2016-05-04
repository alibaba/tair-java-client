package com.taobao.tair.extend.packet.list.response;

import java.nio.BufferUnderflowException;

import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.LeftOrRight;
import com.taobao.tair.extend.packet.ResponsePacketInterface;
import com.taobao.tair.packet.BasePacket;

public class ResponseLRPushPacket extends BasePacket 
		implements ResponsePacketInterface {
	/*memory
	 *	uint32_t config_version;
     *	uint32_t resCode;
     *	uint32_t pushed_num;
     *	uint32_t list_len;
	 */
	private int configVersion;
	private int resCode;
	private int pushedNum;
	private int listLen;
	
    public ResponseLRPushPacket(Transcoder transcoder, LeftOrRight lr) {
    	super(transcoder);
		if (lr == LeftOrRight.IS_L)
			this.pcode = TairConstant.TAIR_RESP_LPUSH_PACKET;
		else
			this.pcode = TairConstant.TAIR_RESP_RPUSH_PACKET;
	}

    public boolean decode() {
    	try {
	    	configVersion 	= byteBuffer.getInt();
	    	resCode 		= byteBuffer.getInt();
	    	pushedNum		= byteBuffer.getInt();
	    	listLen			= byteBuffer.getInt();
    	} catch (BufferUnderflowException e) {
    		//TODO: log warning
    		resCode =  ResultCode.SERIALIZEERROR.getCode();
			return false;
    	}
        return true;
    }

    public int getConfigVersion() {
        return configVersion;
    }

	public int getResCode() {
		return resCode;
	}
	
	public int getPushedNum() {
		return pushedNum;
	}
	
	public int getListLen() {
		return listLen;
	}
}
