package com.taobao.tair.extend;

import java.io.Serializable;

import com.taobao.tair.Result;
import com.taobao.tair.ResultCode;

/**
 * Tair 扩展接口，支持ZSet(排序集合)系列操作
 * @author choutian.xmm
 *
 */
public interface TairManagerZset {
	
	/**
	 * 获取zset中某个value的估值
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @param value 对应的value
	 * @return 返回一个DataEnty, key为输入的key, field为输入的value,
	 * 			value为double类型的score，否则返回错误码
	 */
	public Result<DataEntryDouble> zscore(short namespace, Serializable key, Serializable value);
	
	
	/**
	 * 获取排名（从小到大）在start到end之间的value，负数表示反向的范围
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @param start 起始排名
	 * @param end 终止排名
	 * @return 返回一个DataEntryList,key为输入的key,value为从小到大的排列的List<Object>
	 */
	public Result<DataEntryList> zrange(short namespace, Serializable key, int start, int end);
	
	
	/**
	 * 获取排名（从大到小）在start到end之间的value，负数表示反向的范围
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @param start 起始排名
	 * @param end 终止排名
	 * @return 返回一个DataEntryList,key为输入的key,value为从大到小的排列的List<Object>
	 */
	public Result<DataEntryList> zrevrange(short namespace, Serializable key, int start, int end);
	
	
	/**
	 * 获取排名（从小到大）在start到end之间的value
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @param start 起始估值(权值)
	 * @param end 终止估值(权值)
	 * @return 返回一个DataEntryList,key为输入的key,value为从大到小的排列的List<Object>
	 */
	public Result<DataEntryList> zrangebyscore(short namespace, Serializable key,
			double start, double end);
	
	
	/**
	 * 往对应的zset中加入一个带权值的value
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @param value 插入的value
	 * @param score value对应的权值 score支持小数点后6位小数 大于6位将被截短
	 * 截断方式不会进行四舍五入
	 * @param zadd的带版本验证版本，版本不对，返回错误 
	 * @param expire 超时时间  功能参见expire API
	 * @return 返回成功或者失败错误码
	 */
	public ResultCode zadd(short namespace, Serializable key, Serializable value,
			double score, short version, int expire);
	
	
	/**
	 * 返回某个value在对应zset中的排名
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @param value 要获取的value
	 * @return 返回为一个DataEntryLong,key为key,field为输入的value, value为Long型的排名，否则返回错误码
	 */
	public Result<DataEntryLong> zrank(short namespace, Serializable key, Serializable value);
	
	
	/**
	 * 返回某个value在对应zset中的排名
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @param value 要获取的value
	 * @return 返回为一个DataEntryLong,key为key,field为输入的value, value为Long型的排名，否则返回错误码
	 */
	public Result<DataEntryLong> zrevrank(short namespace, Serializable key, Serializable value);
	
	
	/**
	 * 获取对应zset中元素的个数
	 * @param namespace 对应的namespace的名字
	 * @param key 对应的zset的名字
	 * @return 返回为一个DataEntryLong,key为输入的key,value为Long型的个数，否则返回错误码
	 */
	public Result<DataEntryLong> zcard(short namespace, Serializable key);
	
	
	/**
	 * 删除zset中的对应的key/value
	 * @param namespace 对应的namespace的名字
	 * @param version 带版本验证的版本，版本不对，返回错误
	 * @param key 对应的zset的名字
	 * @param value 要删除的对应的value
	 * @param expire 超时时间  功能参见expire API
	 * @return 返回成功或者错误码
	 */
	public ResultCode zrem(short namespace, Serializable key, Serializable value,
			short version, int expire);
	
	
	/**
	 * 删除zset某个权值范围的数据
	 * @param namespace 对应的namespace的名字
	 * @param version 带版本验证的版本，版本不对，返回错误
	 * @param key 对应的zset的名字
	 * @param start 起始的权值 支持小数点后6位
	 * @param end 终止的权值 支持小数点后6位
	 * @param expire 超时时间  功能参见expire API
	 * @return 返回一个DataEntryLong key为输入的key,value为实际删除的个数 ，否则返回错误码
	 */
	public Result<DataEntryLong> zremrangebyscore(short namespace, Serializable key,
			double start, double end, short version, int expire);
	
	
	/**
	 * 按排名来删除某个排名段的元素
	 * @param namespace zset所在的namespace
	 * @param version 带版本验证的版本，版本不对，返回错误
	 * @param key 对应的zset的名字
	 * @param start 起始排名 
	 * @param end 终止排名
	 * @param exopire 超时时间  功能参见expire API
	 * @return 返回一个DataEntryLong, 其key为输出的key,value为删除的元素个数,如果不辞怎奈或其他问题返回相应的错误码
	 */
	public Result<DataEntryLong> zremrangebyrank(short namespace, Serializable key,
			int start, int end, short version, int expire);
	
	/**
	 * 统计位于权值在start到end之间的元素个数
	 * @param namespace zset所在的namespace
	 * @param key 对应的zset的名字
	 * @param start 起始的权值
	 * @param end 终止的权值
	 * @return 返回一个DataEntryLong,其key为输入的key,value为元素个数,如果不存在或其他问题返回相应的错误码
	 */
	public Result<DataEntryLong> zcount(short namespace, Serializable key, double start, double end);
	
	
	/**
	 * 对对应zset的value的对应score
	 * @param namespace zset所在的namespace
	 * @param key  对应zset的名字
	 * @param value 对应zset->key的value的元素
	 * @param addvalue  增加多少 支持小数点后6位
	 * @param version的带版本验证版本，版本不对，返回错误
	 * @param expire 设置超时时间  功能参见expire API
	 * @return 返回一个DataEntryDouble,其key为输入的key,value为输入的value,
	 * 			value为对应score+addvalue的值,如果不存在或其他问题返回相应的错误码
	 */
	public Result<DataEntryDouble> zincrby(short namespace, Serializable key,
			Serializable value, double addvalue, short version, int expire);
}
