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

import com.allen.message.forwarding.constant.CallbackResult;
import com.allen.message.forwarding.constant.ForwardingResult;
import com.allen.message.forwarding.constant.ForwardingWay;
import com.allen.message.forwarding.constant.MessageConstant;
import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwarding4MQ;
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
		messageManagementService.save(messageReceiveDTO, messageConfig);

		// 异步发送消息到MQ
		List<MessageForwarding4MQ> messageForwardings = toMessageForwarding4MQ(messageReceiveDTO, messageConfig);
		ThreadPoolExecutor executor = ThreadPoolExecutorUtil
				.getExecutor(MessageConstant.MESSAGE_FORWARDING_THREAD_POOL_NAME);
		executor.execute(() -> {
			messageForwardings.stream().forEach(e -> sendToMQ(e));
		});
	}

	/**
	 * 转发消息
	 * 
	 * @param messageForwardings
	 */
	@Override
	public void forward(MessageForwarding4MQ messageForwarding) {
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
					MessageForwardingDTO messageFowardingDTO = messageManagementService.getMessageForwarding(messageNo,
							forwardingId);
					if (Objects.isNull(messageFowardingDTO)) {
						LOGGER.error("数据库中不存在对应的转发明细信息，MQ中的转发明细信息：{}", messageForwarding);
						return;
					}
					if (messageFowardingDTO.getForwardingResult() == ForwardingResult.SUCCESS.value()
							|| messageFowardingDTO.getForwardingResult() == ForwardingResult.FAILURE.value()) {
						LOGGER.info("该消息转发已处理为终态，不再进行转发，数据库中的转发明细信息：{}", messageFowardingDTO);
						return;
					}
					if (messageFowardingDTO.getRetryTimes() >= messageForwarding.getMaxRetryTimes()) {
						LOGGER.info("已达到最大重试次数，不再进行转发，数据库中的转发明细信息：{}", messageFowardingDTO);
						MessageForwardingDTO newMessageForwardingDTO = new MessageForwardingDTO();
						newMessageForwardingDTO.setId(messageFowardingDTO.getId());
						newMessageForwardingDTO.setForwardingResult(ForwardingResult.FAILURE.value());
						newMessageForwardingDTO.setUpdateTime(messageFowardingDTO.getUpdateTime());
						messageManagementService.updateForwardingResult(newMessageForwardingDTO);
						return;
					}
					MessageDTO messageDTO = messageManagementService.getMessage(messageNo);
					ForwardingWay forwardingWay = messageForwarding.getForwardingWay();
					boolean forwardingResult = false;
					if (forwardingWay == ForwardingWay.HTTP) {
						forwardingResult = forwardByHttp(messageDTO, messageForwarding);
					} else if (forwardingWay == ForwardingWay.ROCKETMQ) {
						forwardingResult = forwardByRocketMQ(messageDTO, messageForwarding);
					} else if (forwardingWay == ForwardingWay.KAFKA) {
						forwardingResult = forwardByKafka(messageDTO, messageForwarding);
					}
					if (forwardingResult) {
						MessageDTO newMessageDTO = new MessageDTO();
						newMessageDTO.setId(messageDTO.getId());
						newMessageDTO.setMessageNo(messageDTO.getMessageNo());
						newMessageDTO.setForwardingSuccessAmount(messageDTO.getForwardingSuccessAmount() + 1);
						newMessageDTO.setUpdateTime(messageDTO.getUpdateTime());

						MessageForwardingDTO newMessageForwardingDTO = new MessageForwardingDTO();
						newMessageForwardingDTO.setId(messageFowardingDTO.getId());
						newMessageForwardingDTO.setForwardingResult(ForwardingResult.SUCCESS.value());
						newMessageForwardingDTO.setForwardingSucessTime(LocalDateTime.now());
						newMessageForwardingDTO.setRetryTimes(messageFowardingDTO.getRetryTimes() + 1);
						if (messageForwarding.getCallbackRequired() == MessageConstant.YES) {
							newMessageForwardingDTO.setCallbackResult(CallbackResult.PROCESSING.value());
							newMessageForwardingDTO.setCallbackRetryTimes(-1);
						}
						newMessageForwardingDTO.setUpdateTime(messageFowardingDTO.getUpdateTime());

						messageManagementService.updateForwardingResult(newMessageDTO, newMessageForwardingDTO);
					} else {
						LOGGER.error("消息转发失败，消息转发明细信息：{}", messageForwarding);
						MessageForwardingDTO newMessageForwardingDTO = new MessageForwardingDTO();
						newMessageForwardingDTO.setId(messageFowardingDTO.getId());
						int retryTimes = messageFowardingDTO.getRetryTimes() + 1;
						if (retryTimes >= messageForwarding.getMaxRetryTimes()) {
							newMessageForwardingDTO.setForwardingResult(ForwardingResult.FAILURE.value());
							if (messageForwarding.getCallbackRequired() == MessageConstant.YES) {
								newMessageForwardingDTO.setCallbackResult(CallbackResult.PROCESSING.value());
								newMessageForwardingDTO.setCallbackRetryTimes(-1);
							}
						} else {
							newMessageForwardingDTO.setForwardingResult(ForwardingResult.PROCESSING.value());
						}
						newMessageForwardingDTO.setRetryTimes(messageFowardingDTO.getRetryTimes() + 1);
						newMessageForwardingDTO.setUpdateTime(messageFowardingDTO.getUpdateTime());
						messageManagementService.updateForwardingResult(newMessageForwardingDTO);
					}
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
	public void callback(MessageForwarding4MQ messageForwarding) {
		// TODO Auto-generated method stub

	}

	/**
	 * 转换为转发明细DO对象列表
	 * 
	 * @param messageReceive
	 * @param messageConfig
	 * @return
	 */
	private List<MessageForwarding4MQ> toMessageForwarding4MQ(MessageSendingDTO messageReceive,
			MessageConfigDTO messageConfig) {
		List<MessageForwarding4MQ> messageForwardings = new ArrayList<>();
		List<MessageForwardingConfigDTO> forwardingConfigs = messageConfig.getForwardingConfigs();
		for (MessageForwardingConfigDTO forwardingConfig : forwardingConfigs) {
			MessageForwarding4MQ messageForwarding = new MessageForwarding4MQ();
			messageForwarding.setMessageNo(messageReceive.getMessageNo());
			messageForwarding.setMessageId(messageReceive.getMessageId());
			messageForwarding.setForwardingId(forwardingConfig.getId());
			messageForwarding.setSourceSystemId(messageConfig.getSourceSystemId());
			messageForwarding.setTargetSystem(forwardingConfig.getTargetSystem());
			messageForwarding.setForwardingWay(ForwardingWay.instanceOf(forwardingConfig.getForwardingWay()));
			messageForwarding.setTargetAddress(forwardingConfig.getTargetAddress());
			messageForwarding.setMaxRetryTimes(forwardingConfig.getRetryTimes());
			messageForwarding.setCallbackRequired(forwardingConfig.getCallbackRequired());
			messageForwarding.setCallbackUrl(messageConfig.getCallbackUrl());
			messageForwardings.add(messageForwarding);
		}
		return messageForwardings;
	}

	/**
	 * 将转发明细发送到MQ
	 * 
	 * @param messageForwarding
	 */
	private void sendToMQ(MessageForwarding4MQ messageForwarding) {
		try {
			rocketMQProducer.send4Fowarding(JsonUtil.object2Json(messageForwarding));
		} catch (CustomBusinessException e) {
			LOGGER.error("发送转发明细到MQ异常，转发明细：" + messageForwarding, e);
		}
	}

	/**
	 * 通过http接口转发消息
	 * 
	 * @param messageDTO           消息
	 * @param messageForwarding4MQ 通过MQ接收的消息转发信息
	 * @return 转发结果
	 */
	private boolean forwardByHttp(MessageDTO messageDTO, MessageForwarding4MQ messageForwarding4MQ) {
		String messageContent = messageDTO.getMessageContent();
		Map<String, String> httpHeaders = messageDTO.getHttpHeaders();
		String targetAddress = messageForwarding4MQ.getTargetAddress();
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
	 * @param messageForwarding4MQ 通过MQ接收的消息转发信息
	 * @return 转发结果
	 */
	private boolean forwardByRocketMQ(MessageDTO messageDTO, MessageForwarding4MQ messageForwarding4MQ) {
		return false;
	}

	/**
	 * 通过Kafka转发消息
	 * 
	 * @param messageDTO           消息
	 * @param messageForwarding4MQ 通过MQ接收的消息转发信息
	 * @return 转发结果
	 */
	private boolean forwardByKafka(MessageDTO messageDTO, MessageForwarding4MQ messageForwarding4MQ) {
		return false;
	}

}
