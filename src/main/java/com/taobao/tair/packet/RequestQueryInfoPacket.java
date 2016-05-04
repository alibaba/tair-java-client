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

public class RequestQueryInfoPacket extends BasePacket {
	
	
        private int qtype; 
        private String groupName;
        long server_id = 0;
  
    	public RequestQueryInfoPacket(Transcoder transcoder) {
    		super(transcoder);
    		this.pcode = TairConstant.TAIR_REQ_QUERY_INFO_PACKET;
    	}

    	/**
    	 * encode
    	 */
    	public int encode() {
            writePacketBegin(0);
            byteBuffer.putInt(this.qtype);
            writeString(this.groupName);
            byteBuffer.putLong(this.server_id);

            writePacketEnd();


    		return 0;
    	}

    	/**
    	 * decode
    	 */
    	public boolean decode() {
            this.qtype  = byteBuffer.getInt();
            this.groupName   = readString();
            this.server_id = byteBuffer.getLong();
            return true;
    	}
    	
    	 /**
         * 
         * @return the server_id
         */
        public long getServerId() {
            return server_id;
        }

        /**
         * 
         * @param serverId the serverId to set
         */
        public void setServerId(long serverId) {
            this.server_id = serverId;
        }

        /**
         * 
         * @return the groupName
         */
        public String getGroupName() {
            return groupName;
        }

        /**
         * 
         * @param groupName the groupName to set
         */
        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
        
        /**
         * 
         * @return the qtype
         */
        public int getQtype() {
            return qtype;
        }

        /**
         * 
         * @param setQtype the qtype to set
         */
        public void setQtype(int type) {
            this.qtype = type;
        }    

}
