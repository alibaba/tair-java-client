package com.taobao.tair.extend;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

/**
 * tair扩展接口支持hset(哈希集合)操作
 * @author choutian.xmm
 *
 */
public interface TairManagerHset {
	
	/**
	 * 获取对应的hset的所有的field/value对
	 * @param namespace hset所在的namespace
	 * @param key 对应hset的名字
	 * @return 返回DataEntryMap,其key为输入的key,其Value是一个Map<Object,Object>,
	 * 			就是你要取的数据,如果不存在 或其他问题返回相应的错误码
	 */
	public Result<DataEntryMap> hgetall(short namespace, Serializable key);
	
	
	/**
	 * 对对应hset的field的对应value
	 * @param namespace hset所在的namespace
	 * @param key  对应hset的名字
	 * @param field 对应hset->key的field的名字
	 * @param addvalue  增加多少
	 * @param version的带版本验证版本，版本不对，返回错误
	 * @param expire 设置超时时间 功能参见expire API
	 * @return 返回一个DataEntryLong,其key为输入的key,field为输入的field,
	 * 			value为对应value+addvalue的值,如果不存在或其他问题返回相应的错误码
	 */
	public Result<DataEntryLong> hincrby(short namespace, Serializable key,
			Serializable field, int addvalue, short version, int expire);
	
	
	/**
	 * 对对应key的hset插入一组field/value
	 * @param namespace hset所在的namespace
	 * @param key 对应hset的名字
	 * @param field_values field/value集合
	 * @param version hmset的带版本验证版本,版本不对，返回错误
	 * @param expire 超时时间  功能参见expire API
	 * @return 成功或者失败错误
	 */
	public ResultCode hmset(short namespace, Serializable key, 
			Map<? extends Serializable, ? extends Serializable> field_values, short version, int expire);
	
	
	/**
	 * 对对应key的hset插入一对field/value 如果field存在则替换
	 * @param namespace hset所在的namespace
	 * @param key 对应hset的名字
	 * @param field 对应的field
	 * @param value 对应的value
	 * @param version hset的带版本验证版本,版本不对，返回错误
	 * @param expire超时时间  功能参见expire API
	 * @return 成功或者失败错误
	 */
	public ResultCode hset(short namespace, Serializable key,
			Serializable field, Serializable value, short version, int expire);
	
	
	/**
	 * 获取某个key->field的value
	 * @param namespace hset所在的namespace
	 * @param key 对应hset的名字
	 * @param field 对应的field
	 * @return 成功返回一个DataEntrySimple,其key为输入的key,field为输入的field,
	 * 		        value为获得value,否则返回失败
	 */
	public Result<DataEntrySimple> hget(short namespace, Serializable key, Serializable field);
	
	
	/**
	 * 获取一组field对应的value
	 * @param namespace hset所在的namespace
	 * @param key 对应hset的名字
	 * @param fields 要获取的field集合
	 * @return 返回一个DataEntry,其key为输入的key,
	 * 			其value为一个Map<Object,Object>, 其中第一个Object为输入的field,
	 * 			第二个object为对应的value,否则返回失败
	 */
	public Result<DataEntryMap> hmget(short namespace, Serializable key, List<? extends Serializable> fields);
	
	
	/**
	 * 获取key对应的所有的value
	 * @param namespace hset所在的namespace
	 * @param key 对应hset的名字
	 * @return 返回一个DataEntryList,其key为输入的key，其value为一个List<Object>，否则返回失败
	 */
	public Result<DataEntryList> hvals(short namespace, Serializable key);
	
	
	/**
	 * 获取hset中元素个数
	 * @param namespace hset所在的namespace
	 * @param key 对应hset的名字
	 * @return 返回一个DataEntryLong,其key为输入的key,其value为一个Long的长度,否则返回错误
	 */
	public Result<DataEntryLong> hlen(short namespace, Serializable key);
	
	
	/**
	 * 删除key对应的field/value
	 * @param namespace hset所在的namespace
	 * @param key 对应的hset的名字
	 * @param field 对应要删除的field名字
	 * @param version hset的带版本验证版本,版本不对，返回错误 
	 * @param expire超时时间  功能参见expire API
	 * @return 返回成功或者失败
	 */
	public ResultCode hdel(short namespace, Serializable key, Serializable field,
			short version, int expire);
}
