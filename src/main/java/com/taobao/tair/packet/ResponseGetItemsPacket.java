/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.packet;

import java.util.ArrayList;

import com.taobao.tair.DataEntry;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class ResponseGetItemsPacket extends ResponseGetPacket {

	public ResponseGetItemsPacket(Transcoder transcoder) {
		super(transcoder);
		this.pcode = TairConstant.TAIR_RESP_GETITEMS_PACKET;
	}

	public boolean decode() {
        this.configVersion = byteBuffer.getInt();
        resultCode = byteBuffer.getInt();

        int    count   = byteBuffer.getInt();
        int    size    = 0;
        Object key     = null;
        byte[] value   = null;

        this.entryList = new ArrayList<DataEntry>(count);

		for (int i = 0; i < count; i++) {
			DataEntry de = new DataEntry();

			removeMetas();
			de.decodeMeta(byteBuffer);

			size = byteBuffer.getInt();

			if (size > 0) {
				key = transcoder.decode(byteBuffer.array(), byteBuffer
						.position(), size);
				byteBuffer.position(byteBuffer.position() + size);
			}
			de.setKey(key);

			removeMetas();
			new DataEntry().decodeMeta(byteBuffer); // we don't need these
			// data
			size = byteBuffer.getInt();

			if (size > 0) {
				value = new byte[size];
				byteBuffer.get(value);
				
			}
			de.setValue(value);

			this.entryList.add(de);
		}

        return true;
	}
	

}
