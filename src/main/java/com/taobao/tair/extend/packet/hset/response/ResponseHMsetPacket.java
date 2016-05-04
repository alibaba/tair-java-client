package com.taobao.tair.extend.packet.hset.response;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.set.response.ResponseSimplePacket;

public class ResponseHMsetPacket extends ResponseSimplePacket {

	public ResponseHMsetPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_HMSET_PACKET;
	}

}
