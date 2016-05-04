package com.taobao.tair.extend;

import java.io.Serializable;
import java.util.List;

import com.taobao.tair.DataEntryAbstract;


/**
 * double类型元数据，意为返回的value为double类型
 * @author choutian.xmm
 */
public class DataEntryList extends DataEntryAbstract<List<Object>> {

	private short version = 0;
	private Serializable key = null;
	
	/**
	 * 构造函数
	 * @param key
	 * @param value
	 * @param version
	 * @param expire
	 */
	public DataEntryList(Serializable key, List<Object> value, short version) {
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
	public void setValue(List<Object> value) {
		this.value = value;
	}
	
	/**
	 * 获取value
	 */
	public List<Object> getValue() {
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
