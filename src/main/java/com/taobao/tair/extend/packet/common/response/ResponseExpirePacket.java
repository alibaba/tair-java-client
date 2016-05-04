package com.taobao.tair.extend.packet.common.response;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.set.response.ResponseSimplePacket;

public class ResponseExpirePacket  extends ResponseSimplePacket {

	public ResponseExpirePacket(Transcoder transcoder) {
		super(transcoder);
		pcode = TairConstant.TAIR_RESP_EXPIRE_PACKET;
	}

}
