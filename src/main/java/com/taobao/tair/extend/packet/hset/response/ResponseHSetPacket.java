package com.taobao.tair.extend.packet.hset.response;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.set.response.ResponseSimplePacket;

public class ResponseHSetPacket extends ResponseSimplePacket {

	public ResponseHSetPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_HSET_PACKET;
	}

}
