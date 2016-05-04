package com.taobao.tair.extend;

import java.io.Serializable;

import com.taobao.tair.DataEntryAbstract;

/**
 * double类型元数据，意为返回的value为long类型
 * @author choutian.xmm
 */
public class DataEntryType extends DataEntryAbstract<Type> {

	private Serializable key = null;
	
	/**
	 * 构造函数
	 * @param key
	 * @param field
	 * @param value
	 */
	public DataEntryType(Serializable key, Type value) {
		this.key = key;
		this.value = value;
	}
	
	public DataEntryType(Serializable key, long value) {
		this.key = key;
		setValue(value);
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
	 * @param value
	 */
	public void setValue(Type value) {
		this.value = value;
	}
	
	public void setValue(Long value) {
		switch (value.intValue()) {
		case 0:
			this.value = Type.STRING;
			break;
		case 1:
			this.value = Type.LIST;
			break;
		case 2:
			this.value = Type.SET;
			break;
		case 3:
			this.value = Type.ZSET;
			break;
		case 4:
			this.value = Type.HASH;
			break;
		case 16:
			this.value = Type.NONE;
			break;
		case 17:
		default:
			this.value = Type.UNKNOWN;
			break;
		}
	}
	
	
	/**
	 * 获得value
	 */
	public Type getValue() {
		return this.value;
	}
}
