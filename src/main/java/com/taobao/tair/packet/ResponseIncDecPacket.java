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

public class ResponseIncDecPacket extends BasePacket {
    private int configVersion = 0;
    private int value         = 0;

    public ResponseIncDecPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_INCDEC_PACKET;
    }

    /**
     * encode
     */
    public int encode() {
        writePacketBegin(0);

        // body
        byteBuffer.putInt(this.configVersion);
        byteBuffer.putInt(this.value);

        writePacketEnd();

        return 0;
    }

    /**
     * decode
     */
    public boolean decode() {
        this.configVersion = byteBuffer.getInt();
        this.value         = byteBuffer.getInt();
        return true;
    }

    /**
     * 
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * 
     * @param value the value to set
     */
    public void setValue(int value) {
        this.value = value;
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
}
