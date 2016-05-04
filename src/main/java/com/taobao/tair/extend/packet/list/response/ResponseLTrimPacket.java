package com.taobao.tair.extend.packet.list.response;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.set.response.ResponseSimplePacket;

public class ResponseLTrimPacket extends ResponseSimplePacket {
		

	public ResponseLTrimPacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_LTRIM_PACKET;
	}
}
