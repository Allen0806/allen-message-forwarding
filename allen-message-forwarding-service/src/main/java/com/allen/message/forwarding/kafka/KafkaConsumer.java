package com.allen.message.forwarding.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.string.StringUtil;

/**
 * kafka消费者
 * 
 * @author Allen
 * @date 2020年11月19日
 * @since 1.0.0
 */
@Component
public class KafkaConsumer {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	@KafkaListener(topics = "${message.kafka.send.topic}", groupId = "${message.kafka.send.consumer.group}")
	public void consume(String message) {
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
