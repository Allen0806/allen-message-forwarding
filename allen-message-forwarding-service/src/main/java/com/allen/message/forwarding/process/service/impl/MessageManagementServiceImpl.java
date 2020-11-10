package com.allen.message.forwarding.process.service.impl;

import static com.allen.message.forwarding.constant.ResultStatuses.MF_1003;
import static com.allen.message.forwarding.constant.ResultStatuses.MF_1004;
import static com.allen.message.forwarding.constant.ResultStatuses.MF_1005;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.process.dao.MessageDAO;
import com.allen.message.forwarding.process.dao.MessageForwardingDAO;
import com.allen.message.forwarding.process.model.AmfMessageDO;
import com.allen.message.forwarding.process.model.AmfMessageForwardingDO;
import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.json.JsonUtil;

/**
 * 消息管理服务实现类，封装对数据库层的操作
 * 
 * @author Allen
 * @date 2020年11月6日
 * @since 1.0.0
 */
public class MessageManagementServiceImpl implements MessageManagementService {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageManagementServiceImpl.class);

	/**
	 * 消息DAO层实例
	 */
	@Autowired
	private MessageDAO messageDAO;

	/**
	 * 消息转发DAO层实例
	 */
	@Autowired
	private MessageForwardingDAO messageForwardingDAO;

	@Transactional
	@Override
	public void save(MessageSendingDTO messageReceiveDTO, MessageConfigDTO messageConfigDTO) {
		AmfMessageDO messageDO = toMessageDO(messageReceiveDTO, messageConfigDTO);
		int messageCount = messageDAO.save(messageDO);
		if (messageCount < 1) {
			LOGGER.error("保存消息信息失败，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(),
					messageReceiveDTO.getMessageId());
			throw new CustomBusinessException(MF_1003);
		}
		List<AmfMessageForwardingDO> messageForwardingDOList = toMessageForwardingDO(messageReceiveDTO,
				messageConfigDTO);
		if (messageForwardingDOList.isEmpty()) {
			LOGGER.error("未生成消息转发明细信息，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(),
					messageReceiveDTO.getMessageId());
			throw new CustomBusinessException(MF_1004);
		}
		int forwardingCount = messageForwardingDAO.save(messageForwardingDOList);
		if (forwardingCount != messageForwardingDOList.size()) {
			LOGGER.error("保存消息转发明细信息失败，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(),
					messageReceiveDTO.getMessageId());
			throw new CustomBusinessException(MF_1005);
		}
	}

	/**
	 * 转换未消息DO对象
	 * 
	 * @param messageReceiveDTO
	 * @param messageConfig
	 * @return
	 */
	private AmfMessageDO toMessageDO(MessageSendingDTO messageReceiveDTO, MessageConfigDTO messageConfig) {
		String httpHeaderJson = null;
		Map<String, String> httpHeaders = messageReceiveDTO.getHttpHeaders();
		if (httpHeaders != null && !httpHeaders.isEmpty()) {
			httpHeaderJson = JsonUtil.object2Json(httpHeaders);
		}

		AmfMessageDO messageDO = new AmfMessageDO();
		messageDO.setMessageNo(messageReceiveDTO.getMessageNo());
		messageDO.setMessageKeyword(messageReceiveDTO.getMessageKeyword());
		messageDO.setMessageId(messageReceiveDTO.getMessageId());
		messageDO.setBusinessLineId(messageReceiveDTO.getBusinessLineId());
		messageDO.setSourceSystemId(messageReceiveDTO.getSourceSystemId());
		messageDO.setHttpHeaders(httpHeaderJson);
		messageDO.setMessageContent(messageReceiveDTO.getMessageContent());
		messageDO.setForwardingTotalAmount(messageConfig.getForwardingConfigs().size());
		messageDO.setForwardingSuccessAmount(0);
		return messageDO;
	}

	/**
	 * 转换为转发明细DO对象列表
	 * 
	 * @param messageReceiveDTO
	 * @param messageConfig
	 * @return
	 */
	private List<AmfMessageForwardingDO> toMessageForwardingDO(MessageSendingDTO messageReceiveDTO,
			MessageConfigDTO messageConfig) {
		List<AmfMessageForwardingDO> messageForwardingDOList = new ArrayList<>();
		List<MessageForwardingConfigDTO> forwardingConfigs = messageConfig.getForwardingConfigs();
		for (MessageForwardingConfigDTO forwardingConfig : forwardingConfigs) {
			AmfMessageForwardingDO messageForwardingDO = new AmfMessageForwardingDO();
			messageForwardingDO.setMessageNo(messageReceiveDTO.getMessageNo());
			messageForwardingDO.setMessageKeyword(messageReceiveDTO.getMessageKeyword());
			messageForwardingDO.setMessageId(messageReceiveDTO.getMessageId());
			messageForwardingDO.setForwardingId(forwardingConfig.getId());
			messageForwardingDO.setRetryTimes(0);
			messageForwardingDOList.add(messageForwardingDO);
		}
		return messageForwardingDOList;
	}

}
