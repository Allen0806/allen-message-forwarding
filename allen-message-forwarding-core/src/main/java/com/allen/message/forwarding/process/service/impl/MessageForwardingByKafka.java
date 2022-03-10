package com.allen.message.forwarding.process.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;

/**
 * 通过Kafka转发消息实现类
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Service("messageForwardingByKafka")
public class MessageForwardingByKafka implements MessageForwarding {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardingByKafka.class);

	/**
	 * 消息管理服务
	 */
	@Autowired
	private MessageManagementService messageManagementService;

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	/**
	 * kafka客户端生产者模版
	 */
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Override
	public void forward(MessageForwardingDTO messageForwardingDTO) {
		MessageDTO messageDTO = messageManagementService.getMessage(messageForwardingDTO.getMessageNo());
		String messageContent = messageDTO.getMessageContent();
		String targetAddress = messageForwardingDTO.getTargetAddress();
		ListenableFuture<SendResult<String, String>> resultListenableFuture = kafkaTemplate.send(targetAddress,
				messageContent);
		resultListenableFuture.addCallback(successCallback -> messageProcessService
				.updateForwardingResult(messageForwardingDTO, Boolean.TRUE, Boolean.TRUE), failureCallback -> {
					LOGGER.error("通过Kafka转发消息失败，转发明细信息：" + messageForwardingDTO);
					messageProcessService.updateForwardingResult(messageForwardingDTO, Boolean.FALSE, Boolean.TRUE);
				});
	}
}
