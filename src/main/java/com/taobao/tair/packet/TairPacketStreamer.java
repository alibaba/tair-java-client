/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.nio.ByteBuffer;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;
import com.taobao.tair.extend.packet.LeftOrRight;
import com.taobao.tair.extend.packet.common.response.ResponseExpirePacket;
import com.taobao.tair.extend.packet.common.response.ResponseTTLPacket;
import com.taobao.tair.extend.packet.common.response.ResponseTypePacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHDelPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHGetPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHGetallPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHIncrbyPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHLenPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHMgetPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHMsetPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHSetPacket;
import com.taobao.tair.extend.packet.hset.response.ResponseHValsPacket;
import com.taobao.tair.extend.packet.list.response.ResponseLIndexPacket;
import com.taobao.tair.extend.packet.list.response.ResponseLLenPacket;
import com.taobao.tair.extend.packet.list.response.ResponseLRPopPacket;
import com.taobao.tair.extend.packet.list.response.ResponseLRPushPacket;
import com.taobao.tair.extend.packet.list.response.ResponseLRangePacket;
import com.taobao.tair.extend.packet.list.response.ResponseLRemPacket;
import com.taobao.tair.extend.packet.list.response.ResponseLTrimPacket;

import com.taobao.tair.extend.packet.set.response.ResponseSAddPacket;
import com.taobao.tair.extend.packet.set.response.ResponseSCardPacket;
import com.taobao.tair.extend.packet.set.response.ResponseSMembersPacket;
import com.taobao.tair.extend.packet.set.response.ResponseSPopPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZAddPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZCardPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZCountPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZIncrbyPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRangePacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRangebyscorePacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRankPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRemPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRemrangebyrankPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRemrangebyscorePacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRevrangePacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZRevrankPacket;
import com.taobao.tair.extend.packet.zet.response.ResponseZScorePacket;

public class TairPacketStreamer implements PacketStreamer {
	private Transcoder transcoder = null;

	public TairPacketStreamer(Transcoder transcoder) {
		this.transcoder = transcoder;
	}

	public BasePacket decodePacket(int pcode, byte[] data) {
		BasePacket packet = createPacket(pcode);

		if (packet != null) {
			packet.setLen(data.length);
			packet.setByteBuffer(ByteBuffer.wrap(data));
		}
		return packet;
	}

	private BasePacket createPacket(int pcode) {
		BasePacket packet = null;

		switch (pcode) {
		case TairConstant.TAIR_RESP_RETURN_PACKET:
			packet = new ReturnPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_GET_PACKET:
			packet = new ResponseGetPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_INCDEC_PACKET:
			packet = new ResponseIncDecPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_GET_GROUP_NEW_PACKET:
			packet = new ResponseGetGroupPacket(null);
			break;
		case TairConstant.TAIR_RESP_GETITEMS_PACKET:
			packet = new ResponseGetItemsPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_QUERY_INFO_PACKET:
			packet = new ResponseQueryInfoPacket(transcoder);
			break;
		///////////////////////////////COMMON///////////////////////////
		case TairConstant.TAIR_RESP_TTL_PACKET:
			packet = new ResponseTTLPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_TYPE_PACKET:
			packet = new ResponseTypePacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_EXPIRE_PACKET:
			packet = new ResponseExpirePacket(transcoder);
			break;
		///////////////////////////////LIST/////////////////////////////
		case TairConstant.TAIR_RESP_LPOP_PACKET:
			packet = new ResponseLRPopPacket(transcoder, LeftOrRight.IS_L);
			break;
		case TairConstant.TAIR_RESP_LPUSH_PACKET:
			packet = new ResponseLRPushPacket(transcoder, LeftOrRight.IS_L);
			break;
		case TairConstant.TAIR_RESP_RPOP_PACKET:
			packet = new ResponseLRPopPacket(transcoder, LeftOrRight.IS_R);
			break;
		case TairConstant.TAIR_RESP_RPUSH_PACKET:
			packet = new ResponseLRPushPacket(transcoder, LeftOrRight.IS_R);
			break;
		case TairConstant.TAIR_RESP_LINDEX_PACKET:
			packet = new ResponseLIndexPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_LLEN_PACKET:
			packet = new ResponseLLenPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_LRANGE_PACKET:
			packet = new ResponseLRangePacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_LREM_PACKET:
			packet = new ResponseLRemPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_LTRIM_PACKET:
			packet = new ResponseLTrimPacket(transcoder);
			break;
		// ///////////////////HSET/////////////////////////////////////
		case TairConstant.TAIR_RESP_HDEL_PACKET:
			packet = new ResponseHDelPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HGETALL_PACKET:
			packet = new ResponseHGetallPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HGET_PACKET:
			packet = new ResponseHGetPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HINCRBY_PACKET:
			packet = new ResponseHIncrbyPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HMGET_PACKET:
			packet = new ResponseHMgetPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HMSET_PACKET:
			packet = new ResponseHMsetPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HSET_PACKET:
			packet = new ResponseHSetPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HVALS_PACKET:
			packet = new ResponseHValsPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_HLEN_PACKET:
			packet = new ResponseHLenPacket(transcoder);
			break;
		// /////////////////////SET///////////////////////////////////
		case TairConstant.TAIR_RESP_SADD_PACKET:
			packet = new ResponseSAddPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_SCARD_PACKET:
			packet = new ResponseSCardPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_SMEMBERS_PACKET:
			packet = new ResponseSMembersPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_SPOP_PACKET:
			packet = new ResponseSPopPacket(transcoder);
			break;
		// //////////////////ZSET////////////////////////////////////
		case TairConstant.TAIR_RESP_ZADD_PACKET:
			packet = new ResponseZAddPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZCARD_PACKET:
			packet = new ResponseZCardPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZRANGEBYSCORE_PACKET:
			packet = new ResponseZRangebyscorePacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZRANGE_PACKET:
			packet = new ResponseZRangePacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZREVRANGE_PACKET:
			packet = new ResponseZRevrangePacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZRANK_PACKET:
			packet = new ResponseZRankPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZSCORE_PACKET:
			packet = new ResponseZScorePacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZREM_PACKET:
			packet = new ResponseZRemPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZREMRANGEBYRANK_PACKET:
			packet = new ResponseZRemrangebyrankPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZREMRANGEBYSCORE_PACKET:
			packet = new ResponseZRemrangebyscorePacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZREVRANK_PACKET:
			packet = new ResponseZRevrankPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZCOUNT_PACKET:
			packet = new ResponseZCountPacket(transcoder);
			break;
		case TairConstant.TAIR_RESP_ZINCRBY_PACKET:
			packet = new ResponseZIncrbyPacket(transcoder);
			break;
		default:
			throw new IllegalArgumentException("unkonw return packet, pcode: "
					+ pcode);
		}

		if ((packet != null) && (packet.getPcode() != pcode)) {
			packet = null;
		}

		return packet;
	}

}
