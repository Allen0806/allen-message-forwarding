package com.allen.message.forwarding.process.service.impl;

import com.allen.message.forwarding.constant.CallbackWay;
import com.allen.message.forwarding.process.service.MessageCallback;
import com.allen.tool.bean.SpringBeanUtil;

/**
 * 回调服务工厂
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
public class MessageCallbackFactory {

	/**
	 * 根据回调方式获取对应的回调服务，增加新的回调方式时扩展此方法
	 * 
	 * @param callbackWay 回调方式
	 * @return 回调服务
	 */
	public static MessageCallback getService(CallbackWay callbackWay) {
		switch (callbackWay) {
		case HTTP:
			return SpringBeanUtil.getBean("messageForwardingByHttp", MessageCallback.class);
		case ROCKETMQ:
			return SpringBeanUtil.getBean("messageForwardingByRocketMQ", MessageCallback.class);
		case KAFKA:
			return SpringBeanUtil.getBean("messageForwardingByKafka", MessageCallback.class);
		default:
			return null;
		}
	}
}
