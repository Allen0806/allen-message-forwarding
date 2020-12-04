package com.allen.message.forwarding.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.string.StringUtil;

/**
 * 上游发送的需要转发的消息消费者
 * 
 * @author Allen
 * @date 2020年11月11日
 * @since 1.0.0
 */
@Service
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = "${message.rocketmq.send.topic}", consumerGroup = "${message.rocketmq.send.consumer.group}", selectorExpression = "${message.rocketmq.send.tag}")
public class MessageSendingConsumer implements RocketMQListener<String> {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSendingConsumer.class);

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	@Override
	public void onMessage(String message) {
		if (StringUtil.isBlank(message)) {
			return;
		}
		LOGGER.info("上游系统发送的消息：{}", message);
		try {
			MessageSendingDTO messageSending = JsonUtil.json2Object(message, MessageSendingDTO.class);
			messageProcessService.send(messageSending);
		} catch (Exception e) {
			LOGGER.info("消费上游系统发送的消息出错，消息内容：" + message, e);
		}
	}
}
