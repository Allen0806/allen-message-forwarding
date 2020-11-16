package com.allen.message.forwarding.process.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.allen.message.forwarding.constant.CallbackResult;
import com.allen.message.forwarding.constant.ForwardingResult;
import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.process.dao.MessageDAO;
import com.allen.message.forwarding.process.dao.MessageForwardingDAO;
import com.allen.message.forwarding.process.model.AmfMessageDO;
import com.allen.message.forwarding.process.model.AmfMessageForwardingDO;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.string.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;

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
	public void save(MessageDTO messageDTO) {
		if (messageDTO == null) {
			LOGGER.error("消息对象为空，保存消息信息失败");
			throw new CustomBusinessException(ResultStatuses.MF_1003);
		}
		List<MessageForwardingDTO> messageForwardings = messageDTO.getMessageForwardings();
		if (Objects.isNull(messageForwardings) || messageForwardings.isEmpty()) {
			LOGGER.error("消息转发明细信息为空，消息流水号：{}，消息ID：{}", messageDTO.getMessageNo(), messageDTO.getMessageId());
			throw new CustomBusinessException(ResultStatuses.MF_1004);
		}
		AmfMessageDO messageDO = toMessageDO(messageDTO);
		int messageCount = messageDAO.save(messageDO);
		if (messageCount < 1) {
			LOGGER.error("保存消息信息失败，消息流水号：{}，消息ID：{}", messageDTO.getMessageNo(), messageDTO.getMessageId());
			throw new CustomBusinessException(ResultStatuses.MF_1003);
		}
		List<AmfMessageForwardingDO> messageForwardingDOList = toMessageForwardingDO(messageForwardings);
		int forwardingCount = messageForwardingDAO.save(messageForwardingDOList);
		if (forwardingCount != messageForwardingDOList.size()) {
			LOGGER.error("保存消息转发明细信息失败，消息流水号：{}，消息ID：{}", messageDTO.getMessageNo(), messageDTO.getMessageId());
			throw new CustomBusinessException(ResultStatuses.MF_1005);
		}
	}

	public void updateMessage(MessageDTO messageDTO) {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void updateForwardingResult(MessageForwardingDTO messageForwardingDTO) {
		if (Objects.isNull(messageForwardingDTO) || messageForwardingDTO.getId() == null) {
			LOGGER.error("消息转发明细为空或主键为空，转发明细信息：{}", messageForwardingDTO);
			throw new CustomBusinessException(ResultStatuses.MF_1009);
		}
		AmfMessageForwardingDO newMessageForwardingDO = toMessageForwardingDO(messageForwardingDTO);
		int count = messageForwardingDAO.update(newMessageForwardingDO);
		// 更新重试次数
		int updateRetryTimes = 0;
		// 更新时使用更新时间戳做为乐观锁，如果更新失败，则循环重试3次
		while (count == 0) {
			if (updateRetryTimes == 3) {
				LOGGER.error("更新消息转发明细失败，转发明细信息：{}", newMessageForwardingDO);
				throw new CustomBusinessException(ResultStatuses.MF_1009);
			}
			AmfMessageForwardingDO oldMessageForwardingDO = messageForwardingDAO.getById(messageForwardingDTO.getId());
			newMessageForwardingDO = new AmfMessageForwardingDO();
			newMessageForwardingDO.setId(messageForwardingDTO.getId());
			boolean isNotChanged = true;
			if (messageForwardingDTO.getForwardingResult() == ForwardingResult.SUCCESS.value()) {
				if (oldMessageForwardingDO.getForwardingResult() != ForwardingResult.SUCCESS.value()) {
					newMessageForwardingDO.setForwardingResult(ForwardingResult.SUCCESS.value());
					newMessageForwardingDO.setForwardingSucessTime(LocalDateTime.now());
					newMessageForwardingDO.setRetryTimes(oldMessageForwardingDO.getRetryTimes() + 1);
					isNotChanged = false;
				} else {
					LOGGER.error("消息可能已被重复转发，转发明细信息", oldMessageForwardingDO);
				}
			} else if (messageForwardingDTO.getForwardingResult() == ForwardingResult.PROCESSING.value()) {
				if (oldMessageForwardingDO.getForwardingResult() != ForwardingResult.SUCCESS.value()) {
					newMessageForwardingDO.setRetryTimes(oldMessageForwardingDO.getRetryTimes() + 1);
					isNotChanged = false;
				}
			} else if (messageForwardingDTO.getForwardingResult() == ForwardingResult.FAILURE.value()) {
				if (oldMessageForwardingDO.getForwardingResult() == ForwardingResult.PROCESSING.value()) {
					newMessageForwardingDO.setForwardingResult(ForwardingResult.FAILURE.value());
					newMessageForwardingDO.setRetryTimes(oldMessageForwardingDO.getRetryTimes() + 1);
					isNotChanged = false;
				} else if (oldMessageForwardingDO.getForwardingResult() == ForwardingResult.FAILURE.value()) {
					newMessageForwardingDO.setRetryTimes(oldMessageForwardingDO.getRetryTimes() + 1);
					isNotChanged = false;
				}
			}
			// TODO 设置callback信息
			if (isNotChanged) {
				break;
			}
			count = messageForwardingDAO.update(newMessageForwardingDO);
			updateRetryTimes++;
		}
	}

	@Override
	public void updateCallbackResult(MessageForwardingDTO messageForwardingDTO) {
		// TODO Auto-generated method stub
		if (Objects.isNull(messageForwardingDTO) || messageForwardingDTO.getId() == null) {
			LOGGER.error("消息转发明细为空或主键为空，转发明细信息：{}", messageForwardingDTO);
			throw new CustomBusinessException(ResultStatuses.MF_1009);
		}
		AmfMessageForwardingDO newMessageForwardingDO = toMessageForwardingDO(messageForwardingDTO);
		int count = messageForwardingDAO.update(newMessageForwardingDO);
		// 更新重试次数
		int updateRetryTimes = 0;
		// 更新时使用更新时间戳做为乐观锁，如果更新失败，则循环重试3次
		while (count == 0) {
			if (updateRetryTimes == 3) {
				LOGGER.error("更新消息转发明细失败，转发明细信息：{}", newMessageForwardingDO);
				throw new CustomBusinessException(ResultStatuses.MF_1009);
			}
			AmfMessageForwardingDO oldMessageForwardingDO = messageForwardingDAO.getById(messageForwardingDTO.getId());
			newMessageForwardingDO = new AmfMessageForwardingDO();
			newMessageForwardingDO.setId(messageForwardingDTO.getId());
			boolean isNotChanged = true;
			if (messageForwardingDTO.getCallbackResult() == CallbackResult.SUCCESS.value()) {
				if (oldMessageForwardingDO.getCallbackResult() != CallbackResult.SUCCESS.value()) {
					newMessageForwardingDO.setCallbackResult(CallbackResult.SUCCESS.value());
					newMessageForwardingDO.setCallbackSucessTime(LocalDateTime.now());
					newMessageForwardingDO.setCallbackRetryTimes(oldMessageForwardingDO.getCallbackRetryTimes() + 1);
					isNotChanged = false;
				} else {
					LOGGER.error("消息可能已被重复回调，转发明细信息", oldMessageForwardingDO);
				}
			} else if (messageForwardingDTO.getCallbackResult() == CallbackResult.PROCESSING.value()) {
				if (oldMessageForwardingDO.getCallbackResult() != CallbackResult.SUCCESS.value()) {
					newMessageForwardingDO.setCallbackRetryTimes(oldMessageForwardingDO.getCallbackRetryTimes() + 1);
					isNotChanged = false;
				}
			} else if (messageForwardingDTO.getCallbackResult() == CallbackResult.FAILURE.value()) {
				if (oldMessageForwardingDO.getCallbackResult() == CallbackResult.PROCESSING.value()) {
					newMessageForwardingDO.setCallbackResult(CallbackResult.FAILURE.value());
					newMessageForwardingDO.setCallbackRetryTimes(oldMessageForwardingDO.getCallbackRetryTimes() + 1);
					isNotChanged = false;
				} else if (oldMessageForwardingDO.getCallbackResult() == CallbackResult.FAILURE.value()) {
					newMessageForwardingDO.setCallbackRetryTimes(oldMessageForwardingDO.getCallbackRetryTimes() + 1);
					isNotChanged = false;
				}
			}

			if (isNotChanged) {
				break;
			}
			count = messageForwardingDAO.update(newMessageForwardingDO);
			updateRetryTimes++;
		}

	}

	@Override
	public MessageDTO getMessage(String messageNo) {
		if (StringUtil.isBlank(messageNo)) {
			return null;
		}
		AmfMessageDO messageDO = messageDAO.get(messageNo);
		return toMessageDTO(messageDO);
	}

	@Override
	public MessageForwardingDTO getMessageForwarding(String messageNo, Long forwardingId) {
		AmfMessageForwardingDO messageForwardingDO = messageForwardingDAO.get(messageNo, forwardingId);
		return toMessageForwardingDTO(messageForwardingDO);
	}

	/**
	 * 转换为消息DO对象
	 * 
	 * @param messageDTO
	 * @return
	 */
	private AmfMessageDO toMessageDO(MessageDTO messageDTO) {
		String httpHeaderJson = null;
		Map<String, String> httpHeaders = messageDTO.getHttpHeaders();
		if (httpHeaders != null && !httpHeaders.isEmpty()) {
			httpHeaderJson = JsonUtil.object2Json(httpHeaders);
		}

		AmfMessageDO messageDO = new AmfMessageDO();
		messageDO.setMessageNo(messageDTO.getMessageNo());
		messageDO.setMessageKeyword(messageDTO.getMessageKeyword());
		messageDO.setMessageId(messageDTO.getMessageId());
		messageDO.setBusinessLineId(messageDTO.getBusinessLineId());
		messageDO.setSourceSystemId(messageDTO.getSourceSystemId());
		messageDO.setHttpHeaders(httpHeaderJson);
		messageDO.setMessageContent(messageDTO.getMessageContent());
		messageDO.setForwardingTotalAmount(messageDTO.getForwardingTotalAmount());
		messageDO.setForwardingSuccessAmount(messageDTO.getForwardingSuccessAmount());
		return messageDO;
	}

	/**
	 * 转换为转发明细DO对象列表
	 * 
	 * @param messageForwardings
	 * @return
	 */
	private List<AmfMessageForwardingDO> toMessageForwardingDO(List<MessageForwardingDTO> messageForwardings) {
		List<AmfMessageForwardingDO> messageForwardingDOList = new ArrayList<>();
		for (MessageForwardingDTO forwardingConfigDTO : messageForwardings) {
			AmfMessageForwardingDO messageForwardingDO = new AmfMessageForwardingDO();
			messageForwardingDO.setMessageNo(forwardingConfigDTO.getMessageNo());
			messageForwardingDO.setMessageKeyword(forwardingConfigDTO.getMessageKeyword());
			messageForwardingDO.setMessageId(forwardingConfigDTO.getMessageId());
			messageForwardingDO.setForwardingId(forwardingConfigDTO.getForwardingId());
			messageForwardingDO.setForwardingResult(forwardingConfigDTO.getForwardingResult());
			messageForwardingDO.setRetryTimes(forwardingConfigDTO.getRetryTimes());
			messageForwardingDOList.add(messageForwardingDO);
		}
		return messageForwardingDOList;
	}

	/**
	 * 转换为消息DTO对象
	 * 
	 * @param messageDO
	 * @return
	 */
	private MessageDTO toMessageDTO(AmfMessageDO messageDO) {
		if (Objects.isNull(messageDO)) {
			return null;
		}
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setId(messageDO.getId());
		messageDTO.setMessageNo(messageDO.getMessageNo());
		messageDTO.setMessageKeyword(messageDO.getMessageKeyword());
		messageDTO.setBusinessLineId(messageDO.getBusinessLineId());
		messageDTO.setSourceSystemId(messageDO.getSourceSystemId());
		messageDTO.setMessageId(messageDO.getMessageId());
		if (StringUtil.isNotBlank(messageDO.getHttpHeaders())) {
			Map<String, String> httpHeaders = JsonUtil.json2Object(messageDO.getHttpHeaders(),
					new TypeReference<Map<String, String>>() {
					});
			messageDTO.setHttpHeaders(httpHeaders);
		}
		messageDTO.setMessageContent(messageDO.getMessageContent());
		messageDTO.setForwardingTotalAmount(messageDO.getForwardingTotalAmount());
		messageDTO.setForwardingSuccessAmount(messageDO.getForwardingSuccessAmount());
		messageDTO.setCreateTime(messageDO.getCreateTime());
		messageDTO.setUpdateTime(messageDO.getUpdateTime());
		return messageDTO;
	}

	/**
	 * 转发明细DTO转换为DO
	 * 
	 * @param messageForwardingDTO
	 * @return
	 */
	private AmfMessageForwardingDO toMessageForwardingDO(MessageForwardingDTO messageForwardingDTO) {
		if (Objects.isNull(messageForwardingDTO)) {
			return null;
		}
		AmfMessageForwardingDO messageForwardingDO = new AmfMessageForwardingDO();
		messageForwardingDO.setId(messageForwardingDTO.getId());
//		messageForwardingDO.setMessageNo(messageForwardingDTO.getMessageNo());
//		messageForwardingDO.setMessageKeyword(messageForwardingDTO.getMessageKeyword());
//		messageForwardingDO.setMessageId(messageForwardingDTO.getMessageId());
//		messageForwardingDO.setForwardingId(messageForwardingDTO.getForwardingId());
		messageForwardingDO.setForwardingResult(messageForwardingDTO.getForwardingResult());
		messageForwardingDO.setForwardingSucessTime(messageForwardingDTO.getForwardingSucessTime());
		messageForwardingDO.setRetryTimes(messageForwardingDTO.getRetryTimes());
		messageForwardingDO.setCallbackResult(messageForwardingDTO.getCallbackResult());
		messageForwardingDO.setCallbackSucessTime(messageForwardingDTO.getCallbackSucessTime());
		messageForwardingDO.setCallbackRetryTimes(messageForwardingDTO.getCallbackRetryTimes());
//		messageForwardingDO.setCreateTime(messageForwardingDTO.getCreateTime());
		messageForwardingDO.setUpdateTime(messageForwardingDTO.getUpdateTime());
		return messageForwardingDO;
	}

	/**
	 * 转发明细DO转换为DTO
	 * 
	 * @param messageForwardingDO
	 * @return
	 */
	private MessageForwardingDTO toMessageForwardingDTO(AmfMessageForwardingDO messageForwardingDO) {
		if (Objects.isNull(messageForwardingDO)) {
			return null;
		}
		MessageForwardingDTO messageForwardingDTO = new MessageForwardingDTO();
		messageForwardingDTO.setId(messageForwardingDO.getId());
		messageForwardingDTO.setMessageNo(messageForwardingDO.getMessageNo());
		messageForwardingDTO.setMessageKeyword(messageForwardingDO.getMessageKeyword());
		messageForwardingDTO.setMessageId(messageForwardingDO.getMessageId());
		messageForwardingDTO.setForwardingId(messageForwardingDO.getForwardingId());
		messageForwardingDTO.setForwardingResult(messageForwardingDO.getForwardingResult());
		messageForwardingDTO.setForwardingSucessTime(messageForwardingDO.getForwardingSucessTime());
		messageForwardingDTO.setRetryTimes(messageForwardingDO.getRetryTimes());
		messageForwardingDTO.setCallbackResult(messageForwardingDO.getCallbackResult());
		messageForwardingDTO.setCallbackSucessTime(messageForwardingDO.getCallbackSucessTime());
		messageForwardingDTO.setCallbackRetryTimes(messageForwardingDO.getCallbackRetryTimes());
		messageForwardingDTO.setCreateTime(messageForwardingDO.getCreateTime());
		messageForwardingDTO.setUpdateTime(messageForwardingDO.getUpdateTime());
		return messageForwardingDTO;
	}

}
