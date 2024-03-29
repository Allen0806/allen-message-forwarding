package com.allen.message.forwarding.process.service.impl;

import com.allen.message.forwarding.constant.*;
import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.process.model.*;
import com.allen.message.forwarding.process.service.MessageCallback;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.message.forwarding.rocketmq.RocketMQProducer;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.thread.ThreadPoolExecutorUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 消息转发处理服务层接口实现类
 *
 * @author Allen
 * @date 2020年11月5日
 * @since 1.0.0
 */
@Service
@RefreshScope
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
     * 回调处理线程池
     */
    @Autowired
    private ThreadPoolTaskExecutor commonExecutor;

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

    /**
     * 历史消息保留天数
     */
    @Value("${message.retention-days}")
    private Integer retentionDays;

    @Override
    public void send(MessageSendingDTO messageReceiveDTO) {
        Integer messageId = messageReceiveDTO.getMessageId();
        MessageConfigDTO messageConfig = messageConfigService.getByMessageId(messageReceiveDTO.getMessageId());
        if (messageConfig == null || messageConfig.getForwardingConfigs() == null || messageConfig.getForwardingConfigs().isEmpty()) {
            LOGGER.error("根据给定的消息ID未获取到对应的消息配置信息或消息配置信息有误，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(), messageId);
            throw new CustomBusinessException(ResultStatuses.MF_1001);
        }
        if (!messageConfig.getBusinessLineId().equals(messageReceiveDTO.getBusinessLineId()) || !Objects.equals(messageConfig.getSourceSystemId(), messageReceiveDTO.getMessageId())) {
            LOGGER.error("传入的业务线ID及来源系统ID与消息配置信息中的不匹配，消息流水号：{}，消息ID：{}", messageReceiveDTO.getMessageNo(), messageId);
            throw new CustomBusinessException(ResultStatuses.MF_1002);
        }
        MessageDTO messageDTO = toMessageDTO(messageReceiveDTO, messageConfig);
        messageManagementService.save(messageDTO);

        // 异步发送消息到MQ
        ThreadPoolExecutor executor = ThreadPoolExecutorUtil.getExecutor(MessageConstant.MESSAGE_FORWARDING_THREAD_POOL_NAME);
        executor.execute(() -> send2ForwardingMQ(messageReceiveDTO, messageConfig));
    }

    /**
     * 转发消息
     *
     * @param messageForwarding
     */
    @Override
    public void forward(ForwardingMessage4MQDTO messageForwarding) {
        // 1.获取锁，如果失败则返回，key：messageNo+forwardingId，
        // 2.从数据库中获取转发明细，判断是否转发成功或失败
        // 3.转发消息
        // 4.转发成功后如果需要回调，则设置回调信息，发送到回调mq
        // 5.更新转发结果
        String messageNo = messageForwarding.getMessageNo();
        Long forwardingId = messageForwarding.getForwardingId();
        String lockKey = MessageConstant.MESSAGE_FORWARDING_LOCK_NAME + ":" + messageNo + ":" + forwardingId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(30, 30, TimeUnit.SECONDS)) {
                try {
                    MessageForwardingDTO messageForwardingDTO = messageManagementService.getMessageForwarding(messageNo,
                            forwardingId);
                    if (Objects.isNull(messageForwardingDTO)) {
                        LOGGER.error("数据库中不存在对应的转发明细信息，MQ中的转发明细信息：{}", messageForwarding);
                        return;
                    }
                    if (Objects.equals(messageForwardingDTO.getForwardingStatus(), ForwardingStatus.FINISH.value())) {
                        LOGGER.info("该消息转发已处理为终态，不再进行转发，数据库中的转发明细信息：{}", messageForwardingDTO);
                        return;
                    }
                    if (messageForwardingDTO.getForwardingRetryTimes() >= messageForwardingDTO.getMaxRetryTimes()) {
                        LOGGER.info("已达到最大重试次数，不再进行转发，数据库中的转发明细信息：{}", messageForwardingDTO);
                        updateForwardingResult(messageForwardingDTO, false, false);
                        return;
                    }
                    // 根据不同的转发方式转发消息
                    ForwardingWay forwardingWay = ForwardingWay.valueOf(messageForwardingDTO.getForwardingWay());
                    MessageForwarding messageForwardingService = MessageForwardingFactory.getService(forwardingWay);
                    if (Objects.isNull(messageForwardingService)) {
                        // 如果转发方式不正确，则直接更新为转发失败
                        LOGGER.error("消息转发方式不正确，转发明细信息{}", messageForwardingDTO);
                        updateForwardingResult(messageForwardingDTO, false, false);
                        return;
                    }
                    messageForwardingService.forward(messageForwardingDTO);
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
    public void updateForwardingResult(MessageForwardingDTO messageForwardingDTO, Boolean forwardingResult,
                                       Boolean needRetry) {
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
            // 使用 ThreadPoolTaskExecutor 异步执行，也可以调用异步方法
            commonExecutor.submit(() -> send2CallbackMQ(messageForwardingDTO));
        }
    }

    @Override
    public void callback(ForwardingMessage4MQDTO messageForwarding) {
        String messageNo = messageForwarding.getMessageNo();
        Long forwardingId = messageForwarding.getForwardingId();
        String lockKey = MessageConstant.MESSAGE_CALLBACK_LOCK_NAME + "::" + messageNo + "::" + forwardingId;
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
                    if (messageForwardingDTO.getCallbackRequired() == MessageConstant.NO) {
                        LOGGER.info("该消息不需要回调，数据库中的转发明细信息：{}", messageForwardingDTO);
                        return;
                    }
                    if (messageForwardingDTO.getCallbackStatus() == CallbackStatus.FINISH.value()) {
                        LOGGER.info("该消息回调已处理为终态，不再进行回调，数据库中的转发明细信息：{}", messageForwardingDTO);
                        return;
                    }
                    if (messageForwardingDTO.getCallbackRetryTimes() >= messageForwardingDTO.getMaxRetryTimes()) {
                        LOGGER.info("已达到最大重试次数，不再进行回调，数据库中的转发明细信息：{}", messageForwardingDTO);
                        updateCallbackResult(messageForwardingDTO, false, false);
                        return;
                    }
                    MessageCallback messageCallbackService = MessageCallbackFactory.getService(CallbackWay.HTTP);
                    if (Objects.isNull(messageCallbackService)) {
                        // 如果转发方式不正确，则直接更新为转发失败
                        LOGGER.error("消息回调方式不正确，转发明细信息{}", messageForwardingDTO);
                        updateCallbackResult(messageForwardingDTO, false, false);
                        return;
                    }
                    messageCallbackService.callback(messageForwardingDTO);
                } catch (Exception e) {
                    LOGGER.error("消息回调处理异常，消息转发明细：" + messageForwarding, e);
                    throw new CustomBusinessException(ResultStatuses.MF_1012, e);
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            LOGGER.error("消息回调处理异常，消息转发明细：" + messageForwarding, e);
            throw new CustomBusinessException(ResultStatuses.MF_1012, e);
        }
    }

    /**
     * 更新回调结果
     *
     * @param messageForwardingDTO 转发明细
     * @param callbackResult       回调结果
     * @param needRetry            是否需要重试
     */
    @Override
    public void updateCallbackResult(MessageForwardingDTO messageForwardingDTO, Boolean callbackResult,
                                     Boolean needRetry) {
        Integer retryTimes = messageForwardingDTO.getCallbackStatus() == CallbackStatus.RETRYING.value()
                ? messageForwardingDTO.getForwardingRetryTimes() + 1
                : 0;
        MessageForwardingDTO newMessageForwardingDTO = new MessageForwardingDTO();
        newMessageForwardingDTO.setId(messageForwardingDTO.getId());
        newMessageForwardingDTO.setUpdateTime(messageForwardingDTO.getUpdateTime());
        if (callbackResult) {
            newMessageForwardingDTO.setCallbackStatus(CallbackStatus.FINISH.value());
            newMessageForwardingDTO.setCallbackResult(MessageConstant.YES);
            newMessageForwardingDTO.setCallbackSucessTime(LocalDateTime.now());
            if (messageForwardingDTO.getCallbackStatus() == CallbackStatus.RETRYING.value()) {
                newMessageForwardingDTO.setCallbackRetryTimes(retryTimes);
            }
            LOGGER.info("消息回调成功，消息转发明细信息：{}", messageForwardingDTO);
        } else {
            if (!needRetry) {
                // 处理回调前失败的情况
                newMessageForwardingDTO.setCallbackStatus(CallbackStatus.FINISH.value());
                newMessageForwardingDTO.setCallbackResult(MessageConstant.NO);
            } else {
                // 处理回调失败的情况
                if (retryTimes >= messageForwardingDTO.getMaxRetryTimes()) {
                    newMessageForwardingDTO.setCallbackStatus(CallbackStatus.FINISH.value());
                    newMessageForwardingDTO.setCallbackResult(MessageConstant.NO);
                } else {
                    if (messageForwardingDTO.getCallbackStatus() == CallbackStatus.PROCESSING.value()) {
                        newMessageForwardingDTO.setCallbackStatus(CallbackStatus.RETRYING.value());
                    }
                }
                if (messageForwardingDTO.getCallbackStatus() == CallbackStatus.RETRYING.value()) {
                    newMessageForwardingDTO.setCallbackRetryTimes(retryTimes);
                }
                LOGGER.error("消息回调失败，消息转发明细信息：{}", messageForwardingDTO);
            }
        }
        messageManagementService.updateCallbackResult(newMessageForwardingDTO);
    }

    @Override
    public void retryForward() {
        MessageForwardingQueryParamDTO queryParam = new MessageForwardingQueryParamDTO();
        queryParam.setForwardingStatus(ForwardingStatus.RETRYING.value());
        PagingQueryParam<MessageForwardingQueryParamDTO> pagingQueryParam = new PagingQueryParam<>(queryParam, 0, 100);
        while (true){

        }


//        Integer count = messageManagementService.countMessageForwarding(queryParam);
//        if (count == null || count == 0) {
//            return;
//        }
//        Integer pageSize = 1000;
//        queryParam.setPageSize(pageSize);
//        int pageAmount = count / 1000 + 1;
//        for (int i = 0; i < pageAmount; i++) {
//            Integer startNo = i * pageSize;
//            queryParam.setStartNo(startNo);
//            List<MessageForwardingDTO> messageForwardings = messageManagementService.listMessageForwarding4Paging(queryParam);
//            // 异步发送消息到MQ，采用批量发送消息
//            ThreadPoolExecutor executor = ThreadPoolExecutorUtil
//                    .getExecutor(MessageConstant.MESSAGE_FORWARDING_THREAD_POOL_NAME);
//            executor.execute(() -> send2ForwardingMQ(messageForwardings));
//        }
    }

    @Override
    public void retryCallback() {
//        MessageForwardingQueryParamDTO queryParam = new MessageForwardingQueryParamDTO();
//        queryParam.setForwardingStatus(ForwardingStatus.FINISH.value());
//        queryParam.setCallbackStatus(CallbackStatus.RETRYING.value());
//        Integer count = messageManagementService.countMessageForwarding(queryParam);
//        if (count == null || count == 0) {
//            return;
//        }
//        Integer pageSize = 1000;
//        queryParam.setPageSize(pageSize);
//        int pageAmount = count / 1000 + 1;
//        for (int i = 0; i < pageAmount; i++) {
//            Integer startNo = i * pageSize;
//            queryParam.setStartNo(startNo);
//            List<MessageForwardingDTO> messageForwardings = messageManagementService.listMessageForwarding(queryParam);
//            // 异步发送消息到MQ，采用批量发送消息
//            commonExecutor.submit(() -> send2CallbackMQ(messageForwardings));
//        }

    }

    @Override
    public void migrate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.minusDays(retentionDays);
        messageManagementService.migrate(deadline);
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
            ForwardingMessage4MQDTO messageForwarding = new ForwardingMessage4MQDTO();
            messageForwarding.setMessageNo(messageReceive.getMessageNo());
            messageForwarding.setMessageId(messageReceive.getMessageId());
            messageForwarding.setForwardingId(forwardingConfig.getId());
            send2ForwardingMQ(messageForwarding);
        }
    }

    /**
     * 将转发明细发送到转发MQ
     *
     * @param messageForwardingDTOList
     */
    private void send2ForwardingMQ(List<MessageForwardingDTO> messageForwardingDTOList) {
        if (Objects.isNull(messageForwardingDTOList) || messageForwardingDTOList.isEmpty()) {
            return;
        }
        List<ForwardingMessage4MQDTO> messageForwardingList = new ArrayList<>(messageForwardingDTOList.size());
        for (MessageForwardingDTO messageForwardingDTO : messageForwardingDTOList) {
            ForwardingMessage4MQDTO messageForwarding = new ForwardingMessage4MQDTO();
            messageForwarding.setMessageNo(messageForwardingDTO.getMessageNo());
            messageForwarding.setMessageId(messageForwardingDTO.getMessageId());
            messageForwarding.setForwardingId(messageForwardingDTO.getForwardingId());
            messageForwardingList.add(messageForwarding);
        }
        try {
            rocketMQProducer.send4Fowarding(messageForwardingList);
        } catch (CustomBusinessException e) {
            LOGGER.error("批量发送转发明细到转发MQ异常", e);
        }
    }

    /**
     * 将转发明细发送到转发MQ
     *
     * @param messageForwardingDTO
     */
    @SuppressWarnings("unused")
    private void send2ForwardingMQ(MessageForwardingDTO messageForwardingDTO) {
        if (Objects.isNull(messageForwardingDTO)) {
            return;
        }
        ForwardingMessage4MQDTO messageForwarding = new ForwardingMessage4MQDTO();
        messageForwarding.setMessageNo(messageForwardingDTO.getMessageNo());
        messageForwarding.setMessageId(messageForwardingDTO.getMessageId());
        messageForwarding.setForwardingId(messageForwardingDTO.getForwardingId());
        send2ForwardingMQ(messageForwarding);
    }

    /**
     * 将转发明细发送到转发MQ
     *
     * @param messageForwarding
     */
    private void send2ForwardingMQ(ForwardingMessage4MQDTO messageForwarding) {
        try {
            rocketMQProducer.send4Fowarding(messageForwarding);
        } catch (CustomBusinessException e) {
            LOGGER.error("发送转发明细到转发MQ异常，转发明细：" + messageForwarding, e);
        }
    }

    /**
     * 将转发明细发送到回调MQ
     *
     * @param messageForwardingDTOList
     */
    private void send2CallbackMQ(List<MessageForwardingDTO> messageForwardingDTOList) {
        if (Objects.isNull(messageForwardingDTOList) || messageForwardingDTOList.isEmpty()) {
            return;
        }
        List<ForwardingMessage4MQDTO> messageForwardingList = new ArrayList<>(messageForwardingDTOList.size());
        for (MessageForwardingDTO messageForwardingDTO : messageForwardingDTOList) {
            ForwardingMessage4MQDTO messageForwarding = new ForwardingMessage4MQDTO();
            messageForwarding.setMessageNo(messageForwardingDTO.getMessageNo());
            messageForwarding.setMessageId(messageForwardingDTO.getMessageId());
            messageForwarding.setForwardingId(messageForwardingDTO.getForwardingId());
            messageForwardingList.add(messageForwarding);
        }
        try {
            rocketMQProducer.send4Callback(messageForwardingList);
        } catch (CustomBusinessException e) {
            LOGGER.error("批量发送转发明细到回调MQ异常", e);
        }
    }

    /**
     * 将转发明细发送到回调MQ
     *
     * @param messageForwardingDTO
     */
    // @Async("callbackExecutor") //异步方法，括号里的是线程池对象名称，可以不给定，用默认的
    private void send2CallbackMQ(MessageForwardingDTO messageForwardingDTO) {
        if (messageForwardingDTO == null) {
            return;
        }
        ForwardingMessage4MQDTO messageForwarding = new ForwardingMessage4MQDTO();
        messageForwarding.setMessageNo(messageForwardingDTO.getMessageNo());
        messageForwarding.setMessageId(messageForwardingDTO.getMessageId());
        messageForwarding.setForwardingId(messageForwardingDTO.getForwardingId());
        try {
            rocketMQProducer.send4Callback(messageForwarding);
        } catch (CustomBusinessException e) {
            LOGGER.error("发送转发明细到回调MQ异常，转发明细：" + messageForwarding, e);
        }
    }
}
