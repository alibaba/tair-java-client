package com.taobao.tair.extend.packet;

public interface RequestPacketInterface {
	public void setNamespace(short namespace);
	public void setVersion(short version);
	public void setExpire(int expire);
	public void setKey(Object key);
}
