package com.taobao.tair.extend;

import java.io.Serializable;

import com.taobao.tair.DataEntryAbstract;

/**
 * double类型元数据，意为返回的value为double类型
 * @author choutian.xmm
 */
public class DataEntryDouble extends DataEntryAbstract<Double> {

	private Serializable key = null;
	private Serializable field = null;
	
	/**
	 * 构造函数
	 * @param key
	 * @param field
	 * @param value
	 * @param version
	 * @param expire
	 */
	public DataEntryDouble(Serializable key, Serializable field, Double value) {
		this.key = key;
		this.field = field;
		this.value = value;
	}
	
	/**
	 * 构造函数
	 * @param key
	 * @param value
	 * @param version
	 * @param expire
	 */
	public DataEntryDouble(Serializable key, Double value) {
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
	 */
	public void setValue(Double value) {
		this.value = value;
	}
	
	/**
	 * 获得value
	 */
	public Double getValue() {
		return this.value;
	}
}
