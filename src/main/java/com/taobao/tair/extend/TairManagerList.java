/**
 * 
 */
package com.taobao.tair.extend;

import java.io.Serializable;
import java.util.List;

import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;



/**
 * Tair 扩展接口，支持list系列操作
 * 
 * @author YeXiang
 * 
 */
public interface TairManagerList {
	/**
	 * 获取list的长度
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @return 返回为一个DataEntryLong,其key为输入的key,value为DataEntryLong,
	 * 			key为输入的key,value为长度
	 */
	public Result<DataEntryLong> llen(short namespace, Serializable key);
	
	/**
	 * 从List的一头开始删除指定个数的元素value
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param count 指定的个数 正数为从头开始 否则从尾开始
	 * @param version  lrem的带版本验证版本，版本不对，返回错误
	 * @param expire 超时时间  功能参见expire API
	 * @return 返回为一个DataEntryLong,其key为输入的key, field为输入的value,
	 * 			其value为Long型的实际删除个数
	 */
	public Result<DataEntryLong> lrem(short namespace, Serializable key,
			Serializable value, int count, short version, int expire);
	
	/**
	 * 从list的右侧(尾部)去掉指定个数的元素
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param count 要去掉的个数
	 * @param version rpop的带版本验证版本，版本不对，返回错误
	 * @param expire 超时时间  功能参见expire API
	 * @return 返回为一个DataEntryList 其key为输入的key,
	 * 			value为List<Object>,为按序pop出来的Object
	 */
	public Result<DataEntryList> rpop(short namespace, Serializable key,
			int count, short version, int expire);
	
	/**
	 * 从list的左侧(头部)去掉指定个数的元素
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param count 要去掉的个数
	 * @param version lpop的带版本验证版本，版本不对，返回错误
	 * @parram expire 超时时间  功能参见expire API
	 * @return 返回为一个DataEntryList 其key为输入的key,
	 * 			value为List<Object>,为按序pop出来的Object
	 */
	public Result<DataEntryList> lpop(short namespace, Serializable key,
			int count, short version, int expire);
	
	/**
	 * 从list的右侧(尾部)加入指定个数的元素
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param vals 要加入的value
	 * @param version rpush的带版本验证版本，版本不对，返回错误
	 * @param expire 超时时间  功能参见expire API
	 * @return 返回成功或者失败错误码
	 */
	public ResultCode rpush(short namespace, Serializable key,
			Serializable value, short version, int expire);
	public ResultCode rpush(short namespace, Serializable key,
			List<Serializable> vals, short version, int expire);
	
	/**
	 * 从list的左侧(头部)加入指定个数的元素
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param vals 要加入的value
	 * @param version lpush的带版本验证版本，版本不对，返回错误
	 * @param expire 超时时间  功能参见expire API
	 * @return 返回成功或者失败错误码
	 */
	public ResultCode lpush(short namespace, Serializable key,
			Serializable value, short version, int expire);
	public ResultCode lpush(short namespace, Serializable key,
			List<Serializable> vals, short version, int expire);
	
	/**
	 * 获取指定位置的元素
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param index 对应的位置 负数表示从尾部开始算
	 * @return 返回一个DataEntrySimple,key为输入的key,field为输入的index,
	 * 			value为对应的元素,null表示不存在
	 */
	public Result<DataEntrySimple> lindex(short namespace, Serializable key, int index);
	
	/**
	 * 获得list某个范围的元素集合
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param start 起始位置 负数表示从尾部开始算
	 * @param end   终止位置 负数表示从尾部开始算
	 * @return 返回一个DataEntryList,其key为输入的key,value为List<Object>
	 */
	public Result<DataEntryList> lrange(short namespace, Serializable key, int start, int end);
	
	/**
	 * 对指定的list进行范围截取
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的list的名字
	 * @param start 起始位置 负数表示从尾部开始算
	 * @param end 终止位置 负数表示从尾部开始算
	 * @param version ltrim的带版本验证版本，版本不对，返回错误
	 * @param expire 超时时间  功能参见expire API
	 * @return 成功或者失败错误码
	 */
	public ResultCode ltrim(short namespace, Serializable key, int start,
			int end, short version, int expire);
}
