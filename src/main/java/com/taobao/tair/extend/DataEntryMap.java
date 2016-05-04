package com.taobao.tair.extend;

import java.io.Serializable;
import java.util.Map;

import com.taobao.tair.DataEntryAbstract;

/**
 * double类型元数据，意为返回的value为Map<Object, Object>类型
 * @author choutian.xmm
 */
public class DataEntryMap extends DataEntryAbstract<Map<Object, Object>> {

	private short version = 0;
	private Serializable key = null;
	
	/**
	 * 构造函数
	 * @param key
	 * @param value
	 * @param version
	 * @param expire
	 */
	public DataEntryMap(Serializable key, Map<Object, Object> value, short version) {
		this.key = key;
		this.value = value;
		this.version = version;
	}
	
	/**
	 * 获得key
	 * @return
	 */
	public Serializable getKey() {
		return this.key;
	}

	/**
	 * 设置value
	 */
	public void setValue(Map<Object, Object> value) {
		this.value = value;
	}
	
	/**
	 * 获得value
	 */
	public Map<Object, Object> getValue() {
		return this.value;
	}
	
	/**
	 * 设置版本号
	 * @param version
	 */
	public void setVersion(short version) {
		this.version = version;
	}
	
	/**
	 * 获得版本号
	 * @return
	 */
	public short getVersion() {
		return this.version;
	}
}
