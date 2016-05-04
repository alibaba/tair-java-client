/**
 * 
 */
package com.taobao.tair.extend;

import java.io.Serializable;

import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

/**
 * Tair 扩展接口，支持list系列操作
 * 
 * @author YeXiang
 * 
 */
public interface TairManagerCommon  {

	
	/**
	 * 给一个key设置超时时间
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的key的名字
	 * @param expiretime 超时时间,单位秒
	 * 1.expiretime > 当前时间 传时间点 如12点05分12秒
	 * 2.expiretime < 0 不对该key，产生任何超时效果
	 * 3.expiretime = 0 取消该key的超时设置，也就是将把该key设置成永远不超时
	 * 4.expiretime <= 当前时间 && expiretime > 0 传时间间隔 比如10s
	 * @return 返回成功或者失败错误码
	 */
	public ResultCode expire(short namespace, Serializable key, int expiretime);
	
	/**
	 * 获得key还有多久会超时
	 * 
	 * @param namespace
	 *            对应的namespace的名字
	 * @param key
	 *            对应的key的名字
	 * @return 返回一个DataEntryLong,其key为输入的key,value为时间, 如果为-1,表示不存在或永远不超时
	 */
	public Result<DataEntryLong> ttl(short namespace, Serializable key);

	/**
	 * 获得key对应的是哪种数据结构
	 * 
	 * @param namespace
	 *            对应的namespace的名字
	 * @param key
	 *            对应的key的名字
	 * @return 返回一个DataEntryLong,其key为输入的key,value为数据结构的类型编号
	 * 		   TYPE_STRING 0
	 *         TYPE_LIST 1 
	 *         TYPE_SET 2 
	 *         TYPE_ZSET 3 
	 *         TYPE_HASH 4 
	 *         TYPE_NONE 16
	 *         TYPE_UNKNOWN 32
	 */
	public Result<DataEntryType> type(short namespace, Serializable key);
}
