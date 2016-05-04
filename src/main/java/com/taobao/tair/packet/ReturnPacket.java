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

public class ReturnPacket extends BasePacket {
    private int    configVersion = 0;
    private int    code = 0;
    private String msg  = null;

    public ReturnPacket(Transcoder transcoder) {
        super(transcoder);
        this.pcode = TairConstant.TAIR_RESP_RETURN_PACKET;
    }

    public int encode() {
        int capacity = ((msg == null) ? 0
                                      : msg.length());

        writePacketBegin(capacity);
        byteBuffer.putInt(this.configVersion);
        byteBuffer.putInt(this.code);
        writeString(this.msg);
        writePacketEnd();

        return 0;
    }

    /**
     * decode
     *
     * @return
     */
    public boolean decode() {
        this.configVersion = byteBuffer.getInt();
        this.code          = byteBuffer.getInt();
        this.msg           = readString();
        return true;
    }

    /**
     * 
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * 
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
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

    /**
     * 
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
