package com.taobao.tair.extend;

import java.io.Serializable;

import com.taobao.tair.DataEntryAbstract;

public class DataEntrySimple extends DataEntryAbstract<Object> {

	private short version = 0;
	private Serializable key = null;
	private Serializable field = null;

	/**
	 * 构造函数
	 * @param key
	 * @param field
	 * @param value
	 * @param version
	 */
	public DataEntrySimple(Serializable key, Serializable field, Object value,
			short version) {
		this.key = key;
		this.field = field;
		this.value = value;
		this.version = version;
	}
	
	/**
	 * 构造函数
	 * @param key
	 * @param value
	 * @param version
	 */
	public DataEntrySimple(Serializable key, Object value, short version) {
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
	 * 获得field
	 * @return
	 */
	public Serializable getField() {
		return this.field;
	}
	
	/**
	 * 设置value
	 * @param value
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	/**
	 * 获得value
	 */
	public Object getValue() {
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
	 * 获得获得版本号
	 * @return
	 */
	public short getVersion() {
		return this.version;
	}
}
