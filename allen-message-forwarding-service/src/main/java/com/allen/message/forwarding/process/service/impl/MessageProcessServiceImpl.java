package com.allen.message.forwarding.process.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allen.message.forwarding.constant.CallbackStatus;
import com.allen.message.forwarding.constant.ForwardingStatus;
import com.allen.message.forwarding.constant.ForwardingWay;
import com.allen.message.forwarding.constant.MessageConstant;
import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.ForwardingMessage4MQ;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.message.forwarding.rocketmq.RocketMQProducer;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.http.HttpClientUtil;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.result.BaseResult;
import com.allen.tool.string.StringUtil;
import com.allen.tool.thread.ThreadPoolExecutorUtil;
import com.fasterxml.jackson.core.type.TypeReference;

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
	 * Redisson客户端实例
	 */
	@Autowired
	private RedissonClient redissonClient;

	/**
	 * RocketMQ生产者实例
	 */
	@Autowired
	private RocketMQProducer rocketMQProducer;

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
	public void send(MessageSendingDTO messageReceiveDTO) {
		Integer messageId = messageReceiveDTO.getMessageId();
		MessageConfigDTO messageConfig = messageConfigService.getByMessageId(messageReceiveDTO.getMessageId());
		if (messageConfig == null || messageConfig.getForwardingConfigs() == null
				|| messageConfig.getForwardingConfigs().isEmpty()) {
			LOGGER.error("根据给定的消息ID未获取到对应的消息配置信息或消息配置信息有误，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(),
					messageId);
			throw new CustomBusinessException(ResultStatuses.MF_1001);
		}
		if (!messageConfig.getBusinessLineId().equals(messageReceiveDTO.getBusinessLineId())
				|| messageConfig.getSourceSystemId() != messageReceiveDTO.getMessageId()) {
			LOGGER.error("传入的业务线ID及来源系统ID与消息配置信息中的不匹配，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(), messageId);
			throw new CustomBusinessException(ResultStatuses.MF_1002);
		}
		MessageDTO messageDTO = toMessageDTO(messageReceiveDTO, messageConfig);
		messageManagementService.save(messageDTO);

		// 异步发送消息到MQ
		ThreadPoolExecutor executor = ThreadPoolExecutorUtil
				.getExecutor(MessageConstant.MESSAGE_FORWARDING_THREAD_POOL_NAME);
		executor.execute(() -> send2ForwardingMQ(messageReceiveDTO, messageConfig));
	}

	/**
	 * 转发消息
	 * 
	 * @param messageForwardings
	 */
	@Override
	public void forward(ForwardingMessage4MQ messageForwarding) {
		// 1.获取锁，如果失败则返回，key：messageNo+forwardingId，
		// 2.从数据库中获取转发明细，判断是否转发成功或失败
		// 3.转发消息
		// 4.转发成功后如果需要回调，则设置回调信息，发送到回调mq
		// 5.更新转发结果
		String messageNo = messageForwarding.getMessageNo();
		Long forwardingId = messageForwarding.getForwardingId();
		String lockKey = MessageConstant.MESSAGE_FORWARDING_LOCK_NAME + "::" + messageNo + "::" + forwardingId;
		RLock lock = redissonClient.getLock(lockKey);
		try {
			if (lock.tryLock(3, 3, TimeUnit.SECONDS)) {
				try {
					MessageForwardingDTO messageForwardingDTO = messageManagementService.getMessageForwarding(messageNo,
							forwardingId);
					if (Objects.isNull(messageForwardingDTO)) {
						LOGGER.error("数据库中不存在对应的转发明细信息，MQ中的转发明细信息：{}", messageForwarding);
						return;
					}
					if (messageForwardingDTO.getForwardingStatus() == ForwardingStatus.FINISH.value()) {
						LOGGER.info("该消息转发已处理为终态，不再进行转发，数据库中的转发明细信息：{}", messageForwardingDTO);
						return;
					}
					if (messageForwardingDTO.getForwardingRetryTimes() >= messageForwardingDTO.getMaxRetryTimes()) {
						LOGGER.info("已达到最大重试次数，不再进行转发，数据库中的转发明细信息：{}", messageForwardingDTO);
						update4ForwardingResult(messageForwardingDTO, false, false);
						return;
					}
					// 根据不同的转发方式转发消息
					MessageDTO messageDTO = messageManagementService.getMessage(messageNo);
					ForwardingWay forwardingWay = ForwardingWay.valueOf(messageForwardingDTO.getForwardingWay());
					boolean forwardingResult = false;
					switch (forwardingWay) {
					case HTTP:
						forwardingResult = forwardByHttp(messageDTO, messageForwardingDTO);
						break;
					case ROCKETMQ:
						forwardingResult = forwardByRocketMQ(messageDTO, messageForwardingDTO);
						break;
					case KAFKA:
						forwardingResult = forwardByKafka(messageDTO, messageForwardingDTO);
						break;
					default:
						// 如果转发方式不正确，则直接更新为转发失败
						LOGGER.error("消息转发方式不正确，转发明细信息{}", messageForwardingDTO);
						update4ForwardingResult(messageForwardingDTO, false, false);
						return;
					}
					update4ForwardingResult(messageForwardingDTO, forwardingResult, true);
				} catch (Exception e) {
					LOGGER.error("消息转发处理异常，消息转发明细：" + messageForwarding, e);
					throw new CustomBusinessException(ResultStatuses.MF_1010, e);
				} finally {
					lock.unlock();
				}
			}
		} catch (Exception e) {
			LOGGER.error("锁处理异常，消息转发明细：" + messageForwarding, e);
			throw new CustomBusinessException(ResultStatuses.MF_1010, e);
		}
	}

	@Override
	public void callback(ForwardingMessage4MQ messageForwarding) {
		// TODO Auto-generated method stub

	}

	/**
	 * 转换为消息DTO对象
	 * 
	 * @param messageReceiveDTO
	 * @param messageConfig
	 * @return
	 */
	private MessageDTO toMessageDTO(MessageSendingDTO messageReceiveDTO, MessageConfigDTO messageConfig) {
		MessageDTO messageDTO = new MessageDTO();
		messageDTO.setMessageNo(messageReceiveDTO.getMessageNo());
		messageDTO.setMessageKeyword(messageReceiveDTO.getMessageKeyword());
		messageDTO.setMessageId(messageReceiveDTO.getMessageId());
		messageDTO.setBusinessLineId(messageReceiveDTO.getBusinessLineId());
		messageDTO.setSourceSystemId(messageReceiveDTO.getSourceSystemId());
		messageDTO.setHttpHeaders(messageReceiveDTO.getHttpHeaders());
		messageDTO.setMessageContent(messageReceiveDTO.getMessageContent());
		messageDTO.setForwardingTotalAmount(messageConfig.getForwardingConfigs().size());
		messageDTO.setForwardingSuccessAmount(0);

		List<MessageForwardingDTO> messageForwardings = new ArrayList<>();
		List<MessageForwardingConfigDTO> forwardingConfigs = messageConfig.getForwardingConfigs();
		for (MessageForwardingConfigDTO forwardingConfig : forwardingConfigs) {
			MessageForwardingDTO messageForwardingDTO = new MessageForwardingDTO();
			messageForwardingDTO.setMessageNo(messageReceiveDTO.getMessageNo());
			messageForwardingDTO.setMessageKeyword(messageReceiveDTO.getMessageKeyword());
			messageForwardingDTO.setMessageId(messageReceiveDTO.getMessageId());
			messageForwardingDTO.setForwardingId(forwardingConfig.getId());
			messageForwardingDTO.setForwardingWay(forwardingConfig.getForwardingWay());
			messageForwardingDTO.setTargetAddress(forwardingConfig.getTargetAddress());
			messageForwardingDTO.setMaxRetryTimes(forwardingConfig.getRetryTimes());
			messageForwardingDTO.setCallbackRequired(forwardingConfig.getCallbackRequired());
			messageForwardingDTO.setCallbackUrl(messageConfig.getCallbackUrl());
			messageForwardingDTO.setForwardingStatus(ForwardingStatus.PROCESSING.value());
			messageForwardingDTO.setForwardingRetryTimes(0);
			messageForwardings.add(messageForwardingDTO);
		}
		messageDTO.setMessageForwardings(messageForwardings);
		return messageDTO;
	}

	/**
	 * 将转发明细发送到转发MQ
	 * 
	 * @param messageReceive
	 * @param messageConfig
	 */
	private void send2ForwardingMQ(MessageSendingDTO messageReceive, MessageConfigDTO messageConfig) {
		List<MessageForwardingConfigDTO> forwardingConfigs = messageConfig.getForwardingConfigs();
		for (MessageForwardingConfigDTO forwardingConfig : forwardingConfigs) {
			ForwardingMessage4MQ messageForwarding = new ForwardingMessage4MQ();
			messageForwarding.setMessageNo(messageReceive.getMessageNo());
			messageForwarding.setMessageId(messageReceive.getMessageId());
			messageForwarding.setForwardingId(forwardingConfig.getId());
			send2ForwardingMQ(messageForwarding);
		}
	}

	/**
	 * 将转发明细发送到转发MQ
	 * 
	 * @param messageForwarding
	 */
	private void send2ForwardingMQ(ForwardingMessage4MQ messageForwarding) {
		try {
			rocketMQProducer.send4Fowarding(JsonUtil.object2Json(messageForwarding));
		} catch (CustomBusinessException e) {
			LOGGER.error("发送转发明细到转发MQ异常，转发明细：" + messageForwarding, e);
		}
	}

	/**
	 * 通过http接口转发消息
	 * 
	 * @param messageDTO           消息
	 * @param messageForwarding4MQ 通过MQ接收的消息转发信息
	 * @return 转发结果
	 */
	private boolean forwardByHttp(MessageDTO messageDTO, MessageForwardingDTO messageForwardingDTO) {
		String messageContent = messageDTO.getMessageContent();
		Map<String, String> httpHeaders = messageDTO.getHttpHeaders();
		String targetAddress = messageForwardingDTO.getTargetAddress();
		try {
			String resultStr = HttpClientUtil.doPost4Json(targetAddress, httpHeaders, messageContent, 3);
			if (StringUtil.isBlank(resultStr)) {
				return false;
			}
			BaseResult<Object> baseResult = JsonUtil.json2Object(resultStr, new TypeReference<BaseResult<Object>>() {
			});
			return baseResult.isSuccessful();
		} catch (Exception e) {
			LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo(), e);
			return false;
		}
	}

	/**
	 * 通过RocketMQ转发消息
	 * 
	 * @param messageDTO           消息
	 * @param messageForwardingDTO 转发明细
	 * @return 转发结果
	 */
	private boolean forwardByRocketMQ(MessageDTO messageDTO, MessageForwardingDTO messageForwardingDTO) {
		return false;
	}

	/**
	 * 通过Kafka转发消息
	 * 
	 * @param messageDTO           消息
	 * @param messageForwardingDTO 转发明细
	 * @return 转发结果
	 */
	private boolean forwardByKafka(MessageDTO messageDTO, MessageForwardingDTO messageForwardingDTO) {
		return false;
	}

	/**
	 * 更新转发结果
	 * 
	 * @param messageForwardingDTO
	 */
	/**
	 * 更新转发结果
	 * 
	 * @param messageForwardingDTO 转发明细
	 * @param forwardingResult     转发结果
	 * @param needRetry            是否需要重试
	 */
	private void update4ForwardingResult(MessageForwardingDTO messageForwardingDTO, boolean forwardingResult,
			boolean needRetry) {
		Integer retryTimes = messageForwardingDTO.getForwardingStatus() == ForwardingStatus.RETRYING.value()
				? messageForwardingDTO.getForwardingRetryTimes() + 1
				: 0;
		MessageForwardingDTO newMessageForwardingDTO = new MessageForwardingDTO();
		newMessageForwardingDTO.setId(messageForwardingDTO.getId());
		newMessageForwardingDTO.setUpdateTime(messageForwardingDTO.getUpdateTime());
		if (forwardingResult) {
			newMessageForwardingDTO.setForwardingStatus(ForwardingStatus.FINISH.value());
			newMessageForwardingDTO.setForwardingResult(MessageConstant.YES);
			newMessageForwardingDTO.setForwardingSucessTime(LocalDateTime.now());
			if (messageForwardingDTO.getForwardingStatus() == ForwardingStatus.RETRYING.value()) {
				newMessageForwardingDTO.setForwardingRetryTimes(retryTimes);
			}
			if (messageForwardingDTO.getCallbackRequired() == MessageConstant.YES) {
				newMessageForwardingDTO.setCallbackStatus(CallbackStatus.PROCESSING.value());
				newMessageForwardingDTO.setCallbackRetryTimes(0);
			}
			LOGGER.info("消息转发成功，消息转发明细信息：{}", messageForwardingDTO);
		} else {
			if (!needRetry) {
				// 处理转发前失败的情况
				newMessageForwardingDTO.setForwardingStatus(ForwardingStatus.FINISH.value());
				newMessageForwardingDTO.setForwardingResult(MessageConstant.NO);
				if (messageForwardingDTO.getCallbackRequired() == MessageConstant.YES) {
					newMessageForwardingDTO.setCallbackStatus(CallbackStatus.PROCESSING.value());
					newMessageForwardingDTO.setCallbackRetryTimes(0);
				}
			} else {
				// 处理转发失败的情况
				if (retryTimes >= messageForwardingDTO.getMaxRetryTimes()) {
					newMessageForwardingDTO.setForwardingStatus(ForwardingStatus.FINISH.value());
					newMessageForwardingDTO.setForwardingResult(MessageConstant.NO);
					if (messageForwardingDTO.getCallbackRequired() == MessageConstant.YES) {
						newMessageForwardingDTO.setCallbackStatus(CallbackStatus.PROCESSING.value());
						newMessageForwardingDTO.setCallbackRetryTimes(0);
					}
				} else {
					if (messageForwardingDTO.getForwardingStatus() == ForwardingStatus.PROCESSING.value()) {
						newMessageForwardingDTO.setForwardingStatus(ForwardingStatus.RETRYING.value());
					}
				}
				if (messageForwardingDTO.getForwardingStatus() == ForwardingStatus.RETRYING.value()) {
					newMessageForwardingDTO.setForwardingRetryTimes(retryTimes);
				}
				LOGGER.error("消息转发失败，消息转发明细信息：{}", messageForwardingDTO);
			}
		}
		messageManagementService.updateForwardingResult(newMessageForwardingDTO);

		// 发送到回调MQ
		if (messageForwardingDTO.getCallbackRequired() == MessageConstant.YES) {
			ForwardingMessage4MQ messageForwarding = new ForwardingMessage4MQ();
			messageForwarding.setMessageNo(messageForwardingDTO.getMessageNo());
			messageForwarding.setMessageId(messageForwardingDTO.getMessageId());
			messageForwarding.setForwardingId(messageForwardingDTO.getForwardingId());
			ThreadPoolExecutor executor = ThreadPoolExecutorUtil
					.getExecutor(MessageConstant.MESSAGE_FORWARDING_THREAD_POOL_NAME);
			executor.execute(() -> send2CallbackMQ(messageForwarding));
		}
	}

	/**
	 * 将转发明细发送到回调MQ
	 * 
	 * @param messageForwarding
	 */
	private void send2CallbackMQ(ForwardingMessage4MQ messageForwarding) {
		try {
			rocketMQProducer.send4Callback(JsonUtil.object2Json(messageForwarding));
		} catch (CustomBusinessException e) {
			LOGGER.error("发送转发明细到回调MQ异常，转发明细：" + messageForwarding, e);
		}
	}

}
