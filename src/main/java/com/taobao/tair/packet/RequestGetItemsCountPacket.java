/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class RequestGetItemsCountPacket extends RequestGetPacket {

	public RequestGetItemsCountPacket(Transcoder transcoder) {
		super(transcoder);
		this.setPcode(TairConstant.TAIR_REQ_GETITEMSCOUNT_PACKET);
	}

}
