package com.taobao.tair.extend;

import java.io.Serializable;

import com.taobao.tair.DataEntryAbstract;

/**
 * double类型元数据，意为返回的value为long类型
 * @author choutian.xmm
 */
public class DataEntryLong extends DataEntryAbstract<Long> {

	private Serializable key = null;
	private Serializable field = null;
	
	/**
	 * 构造函数
	 * @param key
	 * @param field
	 * @param value
	 */
	public DataEntryLong(Serializable key, Serializable field, long value) {
		this.key = key;
		this.field = field;
		this.value = value;
	}
	
	/**
	 * 构造函数
	 * @param key
	 * @param value
	 */
	public DataEntryLong(Serializable key, long value) {
		this.key = key;
		this.value = value;
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
	public void setValue(long value) {
		this.value = value;
	}
	
	/**
	 * 获得value
	 */
	public Long getValue() {
		return this.value;
	}
}
