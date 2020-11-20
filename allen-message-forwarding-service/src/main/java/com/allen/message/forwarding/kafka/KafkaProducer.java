package com.allen.message.forwarding.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * kafka生产者
 * 
 * @author Allen
 * @date 2020年11月19日
 * @since 1.0.0
 */
@Component
public class KafkaProducer {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void send(String topic, String message) {
		ListenableFuture<SendResult<String, String>> resultListenableFuture = kafkaTemplate.send(topic, message);
		resultListenableFuture.addCallback(successCallback -> LOGGER.info("发送消息到kakfa成功"),
				failureCallback -> LOGGER.info("发送消息到kakfa失败"));
	}
}
