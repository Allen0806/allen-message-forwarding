package com.allen.message.forwarding.process.service.impl;

import static com.allen.message.forwarding.constant.ResultStatuses.MF_1001;
import static com.allen.message.forwarding.constant.ResultStatuses.MF_1002;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allen.message.forwarding.constant.MessageConstant;
import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.process.model.MessageForwarding4MQ;
import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.thread.ThreadPoolExecutorUtil;

/**
 * 消息转发处理服务层接口实现类
 * 
 * @author Allen
 * @date 2020年11月5日
 * @since 1.0.0
 */
@Service
public class MessageProcessServiceImpl implements MessageProcessService {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessServiceImpl.class);

	/**
	 * 消息配置服务
	 */
	@Autowired
	private MessageConfigService messageConfigService;

	/**
	 * 消息管理服务
	 */
	@Autowired
	private MessageManagementService messageManagementService;

	@Override
	public void receive(MessageSendingDTO messageReceiveDTO) {
		Integer messageId = messageReceiveDTO.getMessageId();
		MessageConfigDTO messageConfig = messageConfigService.getByMessageId(messageReceiveDTO.getMessageId());
		if (messageConfig == null || messageConfig.getForwardingConfigs() == null
				|| messageConfig.getForwardingConfigs().isEmpty()) {
			LOGGER.error("根据给定的消息ID未获取到对应的消息配置信息或消息配置信息有误，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(),
					messageId);
			throw new CustomBusinessException(MF_1001);
		}
		if (!messageConfig.getBusinessLineId().equals(messageReceiveDTO.getBusinessLineId())
				|| messageConfig.getSourceSystemId() != messageReceiveDTO.getMessageId()) {
			LOGGER.error("传入的业务线ID及来源系统ID与消息配置信息中的不匹配，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(), messageId);
			throw new CustomBusinessException(MF_1002);
		}
		messageManagementService.save(messageReceiveDTO, messageConfig);

		// 异步发送消息到MQ
		List<MessageForwarding4MQ> messageForwardings = toMessageForwardingDTO(messageReceiveDTO, messageConfig);
		ThreadPoolExecutor executor = ThreadPoolExecutorUtil
				.getExecutor(MessageConstant.MESSAGE_FORWARDING_THREAD_POOL_NAME);
		executor.execute(() -> {
			messageForwardings.stream().forEach(e -> forward(e));
		});
	}

	/**
	 * 转发消息
	 * 
	 * @param messageForwardings
	 */
	public void forward(MessageForwarding4MQ messageForwarding) {

	}

	/**
	 * 转换为转发明细DO对象列表
	 * 
	 * @param messageReceive
	 * @param messageConfig
	 * @return
	 */
	private List<MessageForwarding4MQ> toMessageForwardingDTO(MessageSendingDTO messageReceive,
			MessageConfigDTO messageConfig) {
		List<MessageForwarding4MQ> messageForwardings = new ArrayList<>();
		List<MessageForwardingConfigDTO> forwardingConfigs = messageConfig.getForwardingConfigs();
		for (MessageForwardingConfigDTO forwardingConfig : forwardingConfigs) {
			MessageForwarding4MQ messageForwarding = new MessageForwarding4MQ();
			messageForwarding.setMessageNo(messageReceive.getMessageNo());
			messageForwarding.setMessageId(messageReceive.getMessageId());
			messageForwarding.setForwardingId(forwardingConfig.getId());
			messageForwardings.add(messageForwarding);
		}
		return messageForwardings;
	}
}
