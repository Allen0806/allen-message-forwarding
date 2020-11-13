package com.allen.message.forwarding.rocketmq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allen.message.forwarding.process.model.MessageForwarding4MQ;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.string.StringUtil;

/**
 * 转发结果回调消息消费者
 * 
 * @author Allen
 * @date 2020年11月11日
 * @since 1.0.0
 */
@Service
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = "${message.rocketmq.callback.topic}", consumerGroup = "${message.rocketmq.callback.consumer.group}", selectorExpression = "${message.rocketmq.callback.tag}")
public class MessageCallbackConsumer implements RocketMQListener<String> {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallbackConsumer.class);
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
		LOGGER.info("消息转发明细信息：{}", message);
		try {
			MessageForwarding4MQ messageForwarding4MQ = JsonUtil.json2Object(message, MessageForwarding4MQ.class);
			messageProcessService.callback(messageForwarding4MQ);
		} catch (Exception e) {
			LOGGER.info("消费消息转发明细结果回调出错，消息转发明细信息：" + message, e);
		}

	}
}
