package com.allen.message.forwarding.process.service.impl;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.message.forwarding.rocketmq.RocketMQProducer;
import com.allen.tool.exception.CustomBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 通过RocketMQ转发消息实现类
 *
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Service("messageForwardingByRocketMQ")
@RefreshScope
public class MessageForwardingByRocketMQ implements MessageForwarding {

    /**
     * 日志纪录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardingByRocketMQ.class);

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
     * RocketMQ生产者实例
     */
    @Autowired
    private RocketMQProducer rocketMQProducer;

    @Override
    public void forward(MessageForwardingDTO messageForwardingDTO) {
        MessageDTO messageDTO = messageManagementService.getMessage(messageForwardingDTO.getMessageNo());
        String messageContent = messageDTO.getMessageContent();
        String targetAddress = messageForwardingDTO.getTargetAddress();
        boolean forwardingResult = false;
        try {
            rocketMQProducer.send(targetAddress, messageContent, messageDTO.getMessageNo());
            forwardingResult = true;
        } catch (CustomBusinessException e) {
            LOGGER.error("通过RocketMQ转发消息失败，转发明细信息：" + messageForwardingDTO, e);
        }
        messageProcessService.updateForwardingResult(messageForwardingDTO, forwardingResult, Boolean.TRUE);
    }
}
