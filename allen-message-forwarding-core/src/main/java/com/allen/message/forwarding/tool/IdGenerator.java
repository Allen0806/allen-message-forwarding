package com.allen.message.forwarding.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Component;

/**
 * 唯一标识生成器
 *
 * @author Allen
 * @date Jul 30, 2020
 * @since 1.0.0
 */
@Component
public class IdGenerator {

	/**
	 * 当前消息来源系统ID在redis中的缓存key
	 */
	private static final String KEY_AMF_SOURCE_SYSTEM_ID = "key_amf_source_system_id";

	/**
	 * 当前消息ID在redis中的缓存
	 */
	private static final String KEY_AMF_MESSAGE_ID = "key_amf_message_id";

	/**
	 * 来源系统ID初始值
	 */
	private static final int SOURCE_SYSTEM_ID_INIT_VALUE = 1000;

	/**
	 * 消息ID初始值
	 */
	private static final int MESSAGE_ID_INIT_VALUE = 100000;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 生成下一个消息来源系统ID
	 * 
	 * @return 下一个消息来源系统ID
	 */
	public Integer generateSourceSystemId() {
		return generateId(KEY_AMF_SOURCE_SYSTEM_ID, SOURCE_SYSTEM_ID_INIT_VALUE);
	}

	/**
	 * 生成下一个消息ID
	 * 
	 * @return 下一个消息ID
	 */
	public Integer generateMessageId() {
		return generateId(KEY_AMF_MESSAGE_ID, MESSAGE_ID_INIT_VALUE);
	}

	/**
	 * 生成下一个ID
	 * 
	 * @param key       缓存key
	 * @param initValue 初始ID值
	 * @return 下一个ID
	 */
	private Integer generateId(String key, int initValue) {
		RedisAtomicInteger redisAtomicInteger = null;
		Object value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			redisAtomicInteger = new RedisAtomicInteger(key, redisTemplate.getConnectionFactory(), initValue);
		} else {
			redisAtomicInteger = new RedisAtomicInteger(key, redisTemplate.getConnectionFactory());
		}

		return redisAtomicInteger.getAndIncrement();
	}

}
