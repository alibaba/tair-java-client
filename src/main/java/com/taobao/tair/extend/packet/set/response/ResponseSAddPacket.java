package com.taobao.tair.extend.packet.set.response;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class ResponseSAddPacket extends ResponseSimplePacket {

	public ResponseSAddPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_SADD_PACKET;
	}

}
