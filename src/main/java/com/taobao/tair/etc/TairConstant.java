/**
 * (C) 2007-2010 Taobao Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 */
package com.taobao.tair.etc;

public class TairConstant {
    // packet flag
    public static final int TAIR_PACKET_FLAG = 0x6d426454;
    
    // packet code
    // request
    public static final int TAIR_REQ_PUT_PACKET    = 1;
    public static final int TAIR_REQ_GET_PACKET    = 2;
    public static final int TAIR_REQ_REMOVE_PACKET = 3;
    public static final int TAIR_REQ_INVALID_PACKET = 12;
    public static final int TAIR_REQ_INCDEC_PACKET = 11;
    public static final int TAIR_REQ_LOCK_PACKET   = 14;

    // response
    public static final int TAIR_RESP_RETURN_PACKET = 101;
    public static final int TAIR_RESP_GET_PACKET    = 102;
    public static final int TAIR_RESP_INCDEC_PACKET    = 105;

    // config server
    public static final int TAIR_REQ_GET_GROUP_NEW_PACKET  = 1002;
    public static final int TAIR_RESP_GET_GROUP_NEW_PACKET = 1102;
    public static final int TAIR_REQ_QUERY_INFO_PACKET = 1009;
    public static final int TAIR_RESP_QUERY_INFO_PACKET = 1106;
    
    // items
    public static final int TAIR_REQ_ADDITEMS_PACKET = 1400;
    public static final int TAIR_REQ_GETITEMS_PACKET = 1401;
    public static final int TAIR_REQ_REMOVEITEMS_PACKET = 1402;
    public static final int TAIR_REQ_GETANDREMOVEITEMS_PACKET = 1403;
    public static final int TAIR_REQ_GETITEMSCOUNT_PACKET = 1404;
    public static final int TAIR_RESP_GETITEMS_PACKET = 1405;
    
    
    /*
     *  extend for redis 2[12]xx, 
     *  first number 2 means extend packet, 
     *  second number 1 means request, 2 means response
     */
    //list 
    public final static int TAIR_REQ_LPOP_PACKET = 2100;
    public final static int TAIR_RESP_LPOP_PACKET = 2200; 
 
    public final static int TAIR_REQ_LPUSH_PACKET = 2101;
    public final static int TAIR_RESP_LPUSH_PACKET = 2201; 
 
    public final static int TAIR_REQ_RPOP_PACKET = 2102;
    public final static int TAIR_RESP_RPOP_PACKET = 2202;
 
    public final static int TAIR_REQ_RPUSH_PACKET = 2103;
    public final static int TAIR_RESP_RPUSH_PACKET = 2203;
 
    public final static int TAIR_REQ_LPUSHX_PACKET = 2104;
    public final static int TAIR_RESP_LPUSHX_PACKET = 2204;
 
    public final static int TAIR_REQ_RPUSHX_PACKET = 2105;
    public final static int TAIR_RESP_RPUSHX_PACKET = 2205;
 
    public final static int TAIR_REQ_LINDEX_PACKET = 2106;
    public final static int TAIR_RESP_LINDEX_PACKET = 2206;

    public final static int TAIR_REQ_LTRIM_PACKET = 2128;
    public final static int TAIR_RESP_LTRIM_PACKET = 2228;

    public final static int TAIR_REQ_LREM_PACKET = 2129;
    public final static int TAIR_RESP_LREM_PACKET = 2229;

    public final static int TAIR_REQ_LRANGE_PACKET = 2130;
    public final static int TAIR_RESP_LRANGE_PACKET = 2230;
 
    public final static int TAIR_REQ_LLEN_PACKET = 2133;
    public final static int TAIR_RESP_LLEN_PACKET = 2233;
    
    //hset 
    public final static int TAIR_REQ_HGETALL_PACKET = 2107;
    public final static int TAIR_RESP_HGETALL_PACKET = 2207;
 
    public final static int TAIR_REQ_HINCRBY_PACKET = 2108;
    public final static int TAIR_RESP_HINCRBY_PACKET = 2208;
 
    public final static int TAIR_REQ_HMSET_PACKET = 2109;
    public final static int TAIR_RESP_HMSET_PACKET = 2209;
 
    public final static int TAIR_REQ_HSET_PACKET = 2110;
    public final static int TAIR_RESP_HSET_PACKET = 2210;
 
    public final static int TAIR_REQ_HSETNX_PACKET = 2111;
    public final static int TAIR_RESP_HSETNX_PACKET = 2211;
 
    public final static int TAIR_REQ_HGET_PACKET = 2112;
    public final static int TAIR_RESP_HGET_PACKET = 2212;
 
    public final static int TAIR_REQ_HMGET_PACKET = 2113;
    public final static int TAIR_RESP_HMGET_PACKET = 2213;
 
    public final static int TAIR_REQ_HVALS_PACKET = 2114;
    public final static int TAIR_RESP_HVALS_PACKET = 2214;
 
    public final static int TAIR_REQ_HDEL_PACKET = 2115;
    public final static int TAIR_RESP_HDEL_PACKET = 2215;
    
    public final static int TAIR_REQ_HLEN_PACKET = 2136;
    public final static int TAIR_RESP_HLEN_PACKET = 2236;
    
    //set 
    public final static int TAIR_REQ_SCARD_PACKET = 2116;
    public final static int TAIR_RESP_SCARD_PACKET = 2216;
 
    public final static int TAIR_REQ_SMEMBERS_PACKET = 2117;
    public final static int TAIR_RESP_SMEMBERS_PACKET = 2217;
 
    public final static int TAIR_REQ_SADD_PACKET = 2118;
    public final static int TAIR_RESP_SADD_PACKET = 2218;
 
    public final static int TAIR_REQ_SPOP_PACKET = 2119;
    public final static int TAIR_RESP_SPOP_PACKET = 2219;
 
    //zset
    public final static int TAIR_REQ_ZRANGE_PACKET = 2120;
    public final static int TAIR_RESP_ZRANGE_PACKET = 2220;

    public final static int TAIR_REQ_ZREVRANGE_PACKET = 2121;
    public final static int TAIR_RESP_ZREVRANGE_PACKET = 2221;

    public final static int TAIR_REQ_ZSCORE_PACKET = 2122;
    public final static int TAIR_RESP_ZSCORE_PACKET = 2222;

    public final static int TAIR_REQ_ZRANGEBYSCORE_PACKET = 2123;
    public final static int TAIR_RESP_ZRANGEBYSCORE_PACKET = 2223;

    public final static int TAIR_REQ_ZADD_PACKET = 2124;
    public final static int TAIR_RESP_ZADD_PACKET = 2224;

    public final static int TAIR_REQ_ZRANK_PACKET = 2125;
    public final static int TAIR_RESP_ZRANK_PACKET = 2225;

    public final static int TAIR_REQ_ZCARD_PACKET = 2126;
    public final static int TAIR_RESP_ZCARD_PACKET = 2226;

    public final static int TAIR_REQ_ZREM_PACKET = 2137;
    public final static int TAIR_RESP_ZREM_PACKET = 2237;

    public final static int TAIR_REQ_ZREMRANGEBYRANK_PACKET = 2138;
    public final static int TAIR_RESP_ZREMRANGEBYRANK_PACKET = 2238;

    public final static int TAIR_REQ_ZREMRANGEBYSCORE_PACKET = 2139;
    public final static int TAIR_RESP_ZREMRANGEBYSCORE_PACKET = 2239;

    public final static int TAIR_REQ_ZREVRANK_PACKET = 2140;
    public final static int TAIR_RESP_ZREVRANK_PACKET = 2240;

    public final static int TAIR_REQ_ZCOUNT_PACKET = 2141;
    public final static int TAIR_RESP_ZCOUNT_PACKET = 2241;

    public final static int TAIR_REQ_ZINCRBY_PACKET = 2142;
    public final static int TAIR_RESP_ZINCRBY_PACKET = 2242;
    
    //common
    public final static int TAIR_REQ_EXPIRE_PACKET = 2127;
    public final static int TAIR_RESP_EXPIRE_PACKET = 2227;

    public final static int TAIR_REQ_EXPIREAT_PACKET = 2131;
    public final static int TAIR_RESP_EXPIREAT_PACKET = 2231;

    public final static int TAIR_REQ_PERSIST_PACKET = 2132;
    public final static int TAIR_RESP_PERSIST_PACKET = 2232;

    public final static int TAIR_REQ_TTL_PACKET = 2134;
    public final static int TAIR_RESP_TTL_PACKET = 2234;

    public final static int TAIR_REQ_TYPE_PACKET = 2135;
    public final static int TAIR_RESP_TYPE_PACKET = 2235;

    

    // serialize type
    public static final int TAIR_STYPE_INT = 1;
    public static final int TAIR_STYPE_STRING = 2;
    public static final int TAIR_STYPE_BOOL = 3;
    public static final int TAIR_STYPE_LONG = 4;
    public static final int TAIR_STYPE_DATE = 5;
    public static final int TAIR_STYPE_BYTE = 6;
    public static final int TAIR_STYPE_FLOAT = 7;
    public static final int TAIR_STYPE_DOUBLE = 8;
    public static final int TAIR_STYPE_BYTEARRAY = 9;
    public static final int TAIR_STYPE_SERIALIZE = 10;
    public static final int TAIR_STYPE_INCDATA = 11;
    
    public static final int TAIR_PACKET_HEADER_SIZE = 16;
    public static final int TAIR_PACKET_HEADER_BLPOS = 12;
    
    // buffer size
    public static final int INOUT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_TIMEOUT = 2000;
    public static final int DEFAULT_CS_CONN_TIMEOUT = 1000;
    public static final int DEFAULT_CS_TIMEOUT = 500;
    public static final int DEFAULT_WAIT_THREAD = 100;
    
    // etc
    public static final int TAIR_DEFAULT_COMPRESSION_THRESHOLD = 8192;
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final int TAIR_KEY_MAX_LENTH = 1024; // 1KB
    public static final int TAIR_VALUE_MAX_LENGTH =1000000;
    
    public static final int TAIR_MAX_COUNT = 1024;
    public static final int TAIR_MALLOC_MAX = 1 << 20; // 1MB
    
    public static final int NAMESPACE_MAX = 1023;
    
    public static final int ITEM_ALL = 65537;
    
    public static final String INVALUD_SERVERLIST_KEY = "invalidate_server";
    
    public static final int Q_AREA_CAPACITY  = 1;
    public static final int Q_MIG_INFO  = 2;
    public static final int Q_DATA_SEVER_INFO  = 3;
    public static final int Q_GROUP_INFO  = 4;
    public static final int Q_STAT_INFO  = 5;
   

    public final static short 	NOT_CARE_VERSION 	= (short)0;
	public final static int 	NOT_CARE_EXPIRE 	= -1;
	public final static int 	CANCEL_EXPIRE 		= 0;
	
    
    ///////////////////constant//////////////////
    public final static int REQUEST_ENCODE_OK = 0;
	public final static int KEYTOLARGE = 1;
	public final static int VALUETOLARGE = 2;
	public final static int SERIALIZEERROR = 3;
	public final static int DATALENTOOLONG = 4;
	
	public final static int DATA_ENTRY_MAX_SIZE = 1024*1024;
	public final static int LIST_MAX_LEN = 8192;
}

