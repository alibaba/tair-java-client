package com.taobao.tair.extend.packet.zet.response;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.set.response.ResponseSimplePacket;

public class ResponseZRemPacket extends ResponseSimplePacket {
	public ResponseZRemPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_ZREM_PACKET;
	}
}
