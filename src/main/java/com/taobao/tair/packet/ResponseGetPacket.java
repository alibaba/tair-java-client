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
import java.util.List;

import com.taobao.tair.DataEntry;
import com.taobao.tair.ResultCode;
import com.taobao.tair.comm.Transcoder;
import com.taobao.tair.etc.TairConstant;

public class ResponseGetPacket extends BasePacket {
	protected int             configVersion;
    protected List<DataEntry> entryList;
    protected int resultCode;
    protected List<DataEntry> proxiedKeyList;

    public ResponseGetPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_GET_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
    	throw new UnsupportedOperationException();
    }

    /**
     * decode
     */
    public boolean decode() {
        this.configVersion = byteBuffer.getInt();
        resultCode = byteBuffer.getInt();

        int    count   = byteBuffer.getInt();
        int    size    = 0;
        Object key     = null;
        Object value   = null;

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
				try {
				value = transcoder.decode(byteBuffer.array(), byteBuffer
						.position(), size);
				} catch (Throwable e) {
					resultCode = ResultCode.SERIALIZEERROR.getCode();
				}
				byteBuffer.position(byteBuffer.position() + size);
			}
			de.setValue(value);

			this.entryList.add(de);
		}
		
		if (count > 1) {
			
			int pc = byteBuffer.getInt();
			if (pc > 0) {
				proxiedKeyList = new ArrayList<DataEntry>(pc);
				for (int i=0; i<pc; i++) {
					removeMetas();
					DataEntry de = new DataEntry();
					de.decodeMeta(byteBuffer);
					size = byteBuffer.getInt();
					if (size > 0)
						proxiedKeyList.indexOf(transcoder.decode(byteBuffer.array(), byteBuffer
						.position(), size));
					byteBuffer.position(byteBuffer.position() + size);
				}
			}
		}

        return true;
    }

    public List<DataEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<DataEntry> entryList) {
        this.entryList = entryList;
    }

    /**
     * 
     * @return the configVersion
     */
    public int getConfigVersion() {
        return configVersion;
    }

    /**
     * 
     * @param configVersion the configVersion to set
     */
    public void setConfigVersion(int configVersion) {
        this.configVersion = configVersion;
    }

	public int getResultCode() {
		return resultCode;
	}
    
}
