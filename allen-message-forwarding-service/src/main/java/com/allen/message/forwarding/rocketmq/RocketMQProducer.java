package com.allen.message.forwarding.rocketmq;

import javax.annotation.Resource;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.string.StringUtil;

/**
 * RocketMQ生产者
 * 
 * @author Allen
 * @date 2020年11月6日
 * @since 1.0.0
 */
@Component
public class RocketMQProducer {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQProducer.class);

	/**
	 * rocketMQ Template
	 */
	@Resource
	private RocketMQTemplate rocketMQTemplate;

	/**
	 * 消息转发topic
	 */
	@Value("${message.rocketmq.forward.topic}")
	private String forwardingTopic;

	/**
	 * 消息转发tag
	 */
	@Value("${message.rocketmq.forward.tag}")
	private String forwardingTag;

	/**
	 * 消息转发结果回调topic
	 */
	@Value("${message.rocketmq.callback.topic}")
	private String callbackTopic;

	/**
	 * 消息 转发结果回调tag
	 */
	@Value("${message.rocketmq.callback.tag}")
	private String callbackTag;

	/**
	 * 将消息发送的待转发队列，主要用于转发失败重试
	 * 
	 * @param message 消息字符串
	 * @return 发送结果：true|false
	 */
	public boolean send4Fowarding(String message) {
		String destination = forwardingTopic + ":" + forwardingTag;
		return send(destination, message);
	}

	/**
	 * 将消息发送的待回调队列，主要用于回调失败重试
	 * 
	 * @param message 消息字符串
	 * @return 发送结果：true|false
	 */
	public boolean send4Callback(String message) {
		String destination = callbackTopic + ":" + callbackTag;
		return send(destination, message);
	}

	/**
	 * 将消息发送的给定的队列
	 * 
	 * @param destination 给定的队列，组成形式：topic:tag
	 * @param message     消息字符串
	 * @return 发送结果：true|false
	 */
	public boolean send(String destination, String message) {
		check(destination, message);
		try {
			SendResult sendResult = rocketMQTemplate.syncSend(destination, message);
			LOGGER.info("消息发送结果：{}", sendResult);
			return true;
		} catch (Exception e) {
			throw new CustomBusinessException(ResultStatuses.MF_1008, e);
		}
	}

	/**
	 * 校验参数，如果不通过则抛出异常
	 * 
	 * @param destination topic
	 * @param message     消息
	 */
	private void check(String destination, String message) {
		if (StringUtil.isBlank(destination)) {
			throw new CustomBusinessException(ResultStatuses.MF_1006);
		}
		if (StringUtil.isBlank(message)) {
			throw new CustomBusinessException(ResultStatuses.MF_1007);
		}
	}
}
