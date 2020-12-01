package com.allen.message.forwarding.process.service.impl;

import com.allen.message.forwarding.constant.ForwardingWay;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.tool.bean.SpringBeanUtil;

/**
 * 转发服务工厂
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
public class MessageForwardingFactory {

	/**
	 * 根据转发方式获取对应的转发服务
	 * 
	 * @param forwardingWay 转发方式
	 * @return 转发服务
	 */
	public static MessageForwarding getMessageForwardingService(ForwardingWay forwardingWay) {
		switch (forwardingWay) {
		case HTTP:
			return SpringBeanUtil.getBean("messageForwardingByHttp", MessageForwarding.class);
		case ROCKETMQ:
			return SpringBeanUtil.getBean("messageForwardingByRocketMQ", MessageForwarding.class);
		case KAFKA:
			return SpringBeanUtil.getBean("messageForwardingByKafka", MessageForwarding.class);
		default:
			return null;
		}
	}
}
