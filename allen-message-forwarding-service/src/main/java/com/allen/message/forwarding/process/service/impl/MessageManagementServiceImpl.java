package com.allen.message.forwarding.process.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.allen.message.forwarding.constant.CallbackStatus;
import com.allen.message.forwarding.constant.ForwardingStatus;
import com.allen.message.forwarding.constant.MessageConstant;
import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.process.dao.MessageDAO;
import com.allen.message.forwarding.process.dao.MessageForwardingDAO;
import com.allen.message.forwarding.process.model.AmfMessageDO;
import com.allen.message.forwarding.process.model.AmfMessageForwardingDO;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageForwardingQueryParamDTO;
import com.allen.message.forwarding.process.model.MessageQueryParamDTO;
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
			// 可以加延时处理 Thread.sleep(100);
			if (updateRetryTimes == 3) {
				LOGGER.error("更新消息转发明细失败，转发明细信息：{}", newMessageForwardingDO);
				throw new CustomBusinessException(ResultStatuses.MF_1009);
			}
			AmfMessageForwardingDO oldMessageForwardingDO = messageForwardingDAO.getById(messageForwardingDTO.getId());
			newMessageForwardingDO = new AmfMessageForwardingDO();
			newMessageForwardingDO.setId(messageForwardingDTO.getId());
			newMessageForwardingDO.setUpdateTime(oldMessageForwardingDO.getUpdateTime());
			boolean isChanged = false;
			boolean isFinished = false;
			if (messageForwardingDTO.getForwardingStatus() == ForwardingStatus.RETRYING.value()) {
				if (oldMessageForwardingDO.getForwardingResult() == ForwardingStatus.PROCESSING.value()) {
					newMessageForwardingDO.setForwardingStatus(ForwardingStatus.RETRYING.value());
					newMessageForwardingDO.setForwardingRetryTimes(messageForwardingDTO.getForwardingRetryTimes());
					isChanged = true;
				} else if (oldMessageForwardingDO.getForwardingResult() == ForwardingStatus.RETRYING.value()) {
					Integer oldRetryTimes = oldMessageForwardingDO.getForwardingRetryTimes() + 1;
					Integer newRetryTimes = messageForwardingDTO.getForwardingRetryTimes();
					Integer forwardingRetryTimes = !Objects.isNull(newRetryTimes) && (newRetryTimes > oldRetryTimes)
							? newRetryTimes
							: oldRetryTimes;
					if (forwardingRetryTimes >= oldMessageForwardingDO.getMaxRetryTimes()) {
						// 如果重试次数超过最大重试次数，则状态设置为已完成，结果为失败
						newMessageForwardingDO.setForwardingStatus(ForwardingStatus.FINISH.value());
						newMessageForwardingDO.setForwardingResult(MessageConstant.NO);
						isFinished = true;
					}
					newMessageForwardingDO.setForwardingRetryTimes(forwardingRetryTimes);
					isChanged = true;
				}
			} else if (messageForwardingDTO.getForwardingStatus() == ForwardingStatus.FINISH.value()) {
				if (oldMessageForwardingDO.getForwardingResult() == ForwardingStatus.PROCESSING.value()) {
					newMessageForwardingDO.setForwardingStatus(ForwardingStatus.FINISH.value());
					newMessageForwardingDO.setForwardingResult(messageForwardingDTO.getForwardingResult());
					newMessageForwardingDO.setForwardingRetryTimes(messageForwardingDTO.getForwardingRetryTimes());
					isChanged = true;
				} else if (oldMessageForwardingDO.getForwardingResult() == ForwardingStatus.RETRYING.value()) {
					newMessageForwardingDO.setForwardingStatus(ForwardingStatus.FINISH.value());
					newMessageForwardingDO.setForwardingResult(messageForwardingDTO.getForwardingResult());
					Integer oldRetryTimes = oldMessageForwardingDO.getForwardingRetryTimes() + 1;
					Integer newRetryTimes = messageForwardingDTO.getForwardingRetryTimes();
					Integer forwardingRetryTimes = !Objects.isNull(newRetryTimes) && (newRetryTimes > oldRetryTimes)
							? newRetryTimes
							: oldRetryTimes;
					newMessageForwardingDO.setForwardingRetryTimes(forwardingRetryTimes);
					isChanged = true;
				} else if (oldMessageForwardingDO.getForwardingResult() == ForwardingStatus.FINISH.value()) {
					Integer oldRetryTimes = oldMessageForwardingDO.getForwardingRetryTimes() + 1;
					Integer newRetryTimes = messageForwardingDTO.getForwardingRetryTimes();
					Integer forwardingRetryTimes = !Objects.isNull(newRetryTimes) && (newRetryTimes > oldRetryTimes)
							? newRetryTimes
							: oldRetryTimes;
					newMessageForwardingDO.setForwardingRetryTimes(forwardingRetryTimes);
					if (messageForwardingDTO.getForwardingResult() == MessageConstant.YES && oldMessageForwardingDO
							.getForwardingResult() != messageForwardingDTO.getForwardingResult()) {
						newMessageForwardingDO.setForwardingResult(messageForwardingDTO.getForwardingResult());
						newMessageForwardingDO.setForwardingSucessTime(messageForwardingDTO.getForwardingSucessTime());
					}
					isChanged = true;
				}
				isFinished = true;
			}
			if (!isChanged) {
				break;
			}
			// 设置回调信息
			if (isFinished && oldMessageForwardingDO.getCallbackRequired() == MessageConstant.YES
					&& oldMessageForwardingDO.getCallbackStatus() == null) {
				if (messageForwardingDTO.getCallbackStatus() != null) {
					newMessageForwardingDO.setCallbackStatus(messageForwardingDTO.getCallbackStatus());
					newMessageForwardingDO.setCallbackRetryTimes(messageForwardingDTO.getCallbackRetryTimes());
				} else {
					// 此步非必须，如果转发为已完成状态，并且需要回调，则messageForwardingDTO.getCallbackStatus()不能为空
					newMessageForwardingDO.setCallbackStatus(CallbackStatus.PROCESSING.value());
					newMessageForwardingDO.setCallbackRetryTimes(0);
				}
			}
			count = messageForwardingDAO.update(newMessageForwardingDO);
			updateRetryTimes++;
		}
		// 如果转发成功，则更新消息信息的转发成功数量
		if (messageForwardingDTO.getForwardingResult() == MessageConstant.YES) {
			messageDAO.updateSucessAmount(messageForwardingDTO.getMessageNo());
		}
	}

	@Transactional
	@Override
	public void updateCallbackResult(MessageForwardingDTO messageForwardingDTO) {
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
			// 可以加延时处理 Thread.sleep(100);
			if (updateRetryTimes == 3) {
				LOGGER.error("更新消息转发明细失败，转发明细信息：{}", newMessageForwardingDO);
				throw new CustomBusinessException(ResultStatuses.MF_1009);
			}
			AmfMessageForwardingDO oldMessageForwardingDO = messageForwardingDAO.getById(messageForwardingDTO.getId());
			newMessageForwardingDO = new AmfMessageForwardingDO();
			newMessageForwardingDO.setId(messageForwardingDTO.getId());
			newMessageForwardingDO.setUpdateTime(oldMessageForwardingDO.getUpdateTime());
			boolean isChanged = false;
			if (messageForwardingDTO.getCallbackStatus() == CallbackStatus.RETRYING.value()) {
				if (oldMessageForwardingDO.getCallbackResult() == CallbackStatus.PROCESSING.value()) {
					newMessageForwardingDO.setCallbackStatus(CallbackStatus.RETRYING.value());
					newMessageForwardingDO.setCallbackRetryTimes(messageForwardingDTO.getCallbackRetryTimes());
					isChanged = true;
				} else if (oldMessageForwardingDO.getCallbackResult() == CallbackStatus.RETRYING.value()) {
					Integer oldRetryTimes = oldMessageForwardingDO.getCallbackRetryTimes() + 1;
					Integer newRetryTimes = messageForwardingDTO.getCallbackRetryTimes();
					Integer callbackRetryTimes = !Objects.isNull(newRetryTimes) && (newRetryTimes > oldRetryTimes)
							? newRetryTimes
							: oldRetryTimes;
					if (callbackRetryTimes >= oldMessageForwardingDO.getMaxRetryTimes()) {
						// 如果重试次数超过最大重试次数，则状态设置为已完成，结果为失败
						newMessageForwardingDO.setCallbackStatus(CallbackStatus.FINISH.value());
						newMessageForwardingDO.setCallbackResult(MessageConstant.NO);
					}
					newMessageForwardingDO.setCallbackRetryTimes(callbackRetryTimes);
					isChanged = true;
				}
			} else if (messageForwardingDTO.getCallbackStatus() == CallbackStatus.FINISH.value()) {
				if (oldMessageForwardingDO.getCallbackResult() == CallbackStatus.PROCESSING.value()) {
					newMessageForwardingDO.setCallbackStatus(CallbackStatus.FINISH.value());
					newMessageForwardingDO.setCallbackResult(messageForwardingDTO.getCallbackResult());
					newMessageForwardingDO.setCallbackRetryTimes(messageForwardingDTO.getCallbackRetryTimes());
					isChanged = true;
				} else if (oldMessageForwardingDO.getCallbackResult() == CallbackStatus.RETRYING.value()) {
					newMessageForwardingDO.setCallbackStatus(CallbackStatus.FINISH.value());
					newMessageForwardingDO.setCallbackResult(messageForwardingDTO.getCallbackResult());
					Integer oldRetryTimes = oldMessageForwardingDO.getCallbackRetryTimes() + 1;
					Integer newRetryTimes = messageForwardingDTO.getCallbackRetryTimes();
					Integer callbackRetryTimes = !Objects.isNull(newRetryTimes) && (newRetryTimes > oldRetryTimes)
							? newRetryTimes
							: oldRetryTimes;
					newMessageForwardingDO.setCallbackRetryTimes(callbackRetryTimes);
					isChanged = true;
				} else if (oldMessageForwardingDO.getCallbackResult() == CallbackStatus.FINISH.value()) {
					Integer oldRetryTimes = oldMessageForwardingDO.getCallbackRetryTimes() + 1;
					Integer newRetryTimes = messageForwardingDTO.getCallbackRetryTimes();
					Integer callbackRetryTimes = !Objects.isNull(newRetryTimes) && (newRetryTimes > oldRetryTimes)
							? newRetryTimes
							: oldRetryTimes;
					newMessageForwardingDO.setCallbackRetryTimes(callbackRetryTimes);
					if (messageForwardingDTO.getCallbackResult() == MessageConstant.YES
							&& oldMessageForwardingDO.getCallbackResult() != messageForwardingDTO.getCallbackResult()) {
						newMessageForwardingDO.setCallbackResult(messageForwardingDTO.getCallbackResult());
						newMessageForwardingDO.setCallbackSucessTime(messageForwardingDTO.getCallbackSucessTime());
					}
					isChanged = true;
				}
			}
			if (!isChanged) {
				break;
			}
			count = messageForwardingDAO.update(newMessageForwardingDO);
			updateRetryTimes++;
		}

	}

	@Transactional
	@Override
	public void migrate(LocalDateTime deadline) {
		if (deadline == null) {
			return;
		}
		messageDAO.migrate(deadline);
		messageForwardingDAO.migrate(deadline);
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
		if (StringUtil.isBlank(messageNo) || forwardingId == null) {
			return null;
		}
		AmfMessageForwardingDO messageForwardingDO = messageForwardingDAO.get(messageNo, forwardingId);
		return toMessageForwardingDTO(messageForwardingDO);
	}

	@Override
	public Integer countMessage(MessageQueryParamDTO messageQueryParam) {
		if (messageQueryParam == null) {
			LOGGER.error("查询条件为空");
			throw new CustomBusinessException(ResultStatuses.MF_1013);
		}
		return messageDAO.count(messageQueryParam);
	}

	@Override
	public List<MessageDTO> listMessage(MessageQueryParamDTO messageQueryParam) {
		if (messageQueryParam == null) {
			LOGGER.error("查询条件为空");
			throw new CustomBusinessException(ResultStatuses.MF_1013);
		}
		String messageNo = messageQueryParam.getMessageNo();
		Integer pageNo = messageQueryParam.getPageNo();
		Integer startNo = messageQueryParam.getStartNo();
		Integer pageSize = messageQueryParam.getPageSize();
		if (StringUtil.isBlank(messageNo)) {
			if ((pageNo == null && startNo == null) || (startNo != null && startNo < 0)
					|| (pageNo != null && pageNo < 1)) {
				LOGGER.error("当前页数或起始行号不能同时为空或起始行号小于0或者当前页数小于1，查询条件：{}", messageQueryParam);
				throw new CustomBusinessException(ResultStatuses.MF_1014);
			}
			if (pageSize == null || pageSize < 1) {
				LOGGER.error("每页行数不能为空或者小于1，查询条件：{}", messageQueryParam);
				throw new CustomBusinessException(ResultStatuses.MF_1015);
			}
		}
		if (startNo == null) {
			messageQueryParam.setStartNo((pageNo - 1) * pageSize);
		}
		List<AmfMessageDO> messageDOList = messageDAO.list(messageQueryParam);
		return toMessageDTO(messageDOList);
	}

	@Override
	public Integer countMessageForwarding(MessageForwardingQueryParamDTO forwardingQueryParam) {
		if (forwardingQueryParam == null) {
			LOGGER.error("查询条件为空");
			throw new CustomBusinessException(ResultStatuses.MF_1013);
		}
		return messageForwardingDAO.count(forwardingQueryParam);
	}

	@Override
	public List<MessageForwardingDTO> listMessageForwarding(MessageForwardingQueryParamDTO forwardingQueryParam) {
		if (forwardingQueryParam == null) {
			LOGGER.error("查询条件为空");
			throw new CustomBusinessException(ResultStatuses.MF_1013);
		}
		String messageNo = forwardingQueryParam.getMessageNo();
		Integer pageNo = forwardingQueryParam.getPageNo();
		Integer startNo = forwardingQueryParam.getStartNo();
		Integer pageSize = forwardingQueryParam.getPageSize();
		if (StringUtil.isBlank(messageNo)) {
			if ((pageNo == null && startNo == null) || (startNo != null && startNo < 0)
					|| (pageNo != null && pageNo < 1)) {
				LOGGER.error("当前页数或起始行号不能同时为空或起始行号小于0或者当前页数小于1，查询条件：{}", forwardingQueryParam);
				throw new CustomBusinessException(ResultStatuses.MF_1014);
			}
			if (pageSize == null || pageSize < 1) {
				LOGGER.error("每页行数不能为空或者小于1，查询条件：{}", forwardingQueryParam);
				throw new CustomBusinessException(ResultStatuses.MF_1015);
			}
		}
		if (startNo == null) {
			forwardingQueryParam.setStartNo((pageNo - 1) * pageSize);
		}
		List<AmfMessageForwardingDO> forwardingDOList = messageForwardingDAO.list(forwardingQueryParam);
		return toMessageForwardingDTO(forwardingDOList);
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
		for (MessageForwardingDTO messageForwardingDTO : messageForwardings) {
			AmfMessageForwardingDO messageForwardingDO = toMessageForwardingDO(messageForwardingDTO);
			if (!Objects.isNull(messageForwardingDO)) {
				messageForwardingDOList.add(messageForwardingDO);
			}
		}
		return messageForwardingDOList;
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
		messageForwardingDO.setMessageNo(messageForwardingDTO.getMessageNo());
		messageForwardingDO.setMessageKeyword(messageForwardingDTO.getMessageKeyword());
		messageForwardingDO.setMessageId(messageForwardingDTO.getMessageId());
		messageForwardingDO.setForwardingId(messageForwardingDTO.getForwardingId());
		messageForwardingDO.setForwardingWay(messageForwardingDTO.getForwardingWay());
		messageForwardingDO.setTargetAddress(messageForwardingDTO.getTargetAddress());
		messageForwardingDO.setMaxRetryTimes(messageForwardingDTO.getMaxRetryTimes());
		messageForwardingDO.setCallbackRequired(messageForwardingDTO.getCallbackRequired());
		messageForwardingDO.setCallbackUrl(messageForwardingDTO.getCallbackUrl());
		messageForwardingDO.setForwardingStatus(messageForwardingDTO.getForwardingStatus());
		messageForwardingDO.setForwardingResult(messageForwardingDTO.getForwardingResult());
		messageForwardingDO.setForwardingSucessTime(messageForwardingDTO.getForwardingSucessTime());
		messageForwardingDO.setForwardingRetryTimes(messageForwardingDTO.getForwardingRetryTimes());
		messageForwardingDO.setCallbackStatus(messageForwardingDTO.getCallbackStatus());
		messageForwardingDO.setCallbackResult(messageForwardingDTO.getCallbackResult());
		messageForwardingDO.setCallbackSucessTime(messageForwardingDTO.getCallbackSucessTime());
		messageForwardingDO.setCallbackRetryTimes(messageForwardingDTO.getCallbackRetryTimes());
		messageForwardingDO.setCreateTime(messageForwardingDTO.getCreateTime());
		messageForwardingDO.setUpdateTime(messageForwardingDTO.getUpdateTime());
		return messageForwardingDO;
	}

	/**
	 * 转换为消息DTO对象
	 * 
	 * @param messageDOList
	 * @return
	 */
	private List<MessageDTO> toMessageDTO(List<AmfMessageDO> messageDOList) {
		if (messageDOList == null || messageDOList.isEmpty()) {
			return Collections.emptyList();
		}
		return messageDOList.parallelStream().map(e -> toMessageDTO(e)).collect(Collectors.toList());
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
	 * 转发明细DO转换为DTO
	 * 
	 * @param messageForwardingDOList
	 * @return
	 */
	private List<MessageForwardingDTO> toMessageForwardingDTO(List<AmfMessageForwardingDO> messageForwardingDOList) {
		if (messageForwardingDOList == null || messageForwardingDOList.isEmpty()) {
			return Collections.emptyList();
		}
		return messageForwardingDOList.parallelStream().map(e -> toMessageForwardingDTO(e))
				.collect(Collectors.toList());
	}

	/**
	 * 转发明细DO转换为DTO
	 * 
	 * @param messageForwardingDTO
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
		messageForwardingDTO.setForwardingWay(messageForwardingDO.getForwardingWay());
		messageForwardingDTO.setTargetAddress(messageForwardingDO.getTargetAddress());
		messageForwardingDTO.setMaxRetryTimes(messageForwardingDO.getMaxRetryTimes());
		messageForwardingDTO.setCallbackRequired(messageForwardingDO.getCallbackRequired());
		messageForwardingDTO.setCallbackUrl(messageForwardingDO.getCallbackUrl());
		messageForwardingDTO.setForwardingStatus(messageForwardingDO.getForwardingStatus());
		messageForwardingDTO.setForwardingResult(messageForwardingDO.getForwardingResult());
		messageForwardingDTO.setForwardingSucessTime(messageForwardingDO.getForwardingSucessTime());
		messageForwardingDTO.setForwardingRetryTimes(messageForwardingDO.getForwardingRetryTimes());
		messageForwardingDTO.setCallbackStatus(messageForwardingDO.getCallbackStatus());
		messageForwardingDTO.setCallbackResult(messageForwardingDO.getCallbackResult());
		messageForwardingDTO.setCallbackSucessTime(messageForwardingDO.getCallbackSucessTime());
		messageForwardingDTO.setCallbackRetryTimes(messageForwardingDO.getCallbackRetryTimes());
		messageForwardingDTO.setCreateTime(messageForwardingDO.getCreateTime());
		messageForwardingDTO.setUpdateTime(messageForwardingDO.getUpdateTime());
		return messageForwardingDTO;
	}

}
