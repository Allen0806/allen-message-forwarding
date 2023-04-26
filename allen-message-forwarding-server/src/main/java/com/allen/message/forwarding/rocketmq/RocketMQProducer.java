package com.allen.message.forwarding.rocketmq;

import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.process.model.ForwardingMessage4MQDTO;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.string.StringUtil;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RocketMQ生产者
 *
 * @author Allen
 * @date 2020年11月6日
 * @since 1.0.0
 */
@Component
@RefreshScope
public class RocketMQProducer {

    /**
     * 日志纪录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQProducer.class);

    /**
     * rocketMQ Template
     */
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 消息转发topic
     */
    @Value("${allen.message.forwarding.rocketmq.forward.topic}")
    private String forwardingTopic;


    /**
     * 消息转发结果回调topic
     */
    @Value("${allen.message.forwarding.rocketmq.callback.topic}")
    private String callbackTopic;


    /**
     * 将消息发送的待转发队列，主要用于转发失败重试
     *
     * @param messageForwarding 消息转发明细
     * @return 发送结果：true|false
     */
    public boolean send4Fowarding(ForwardingMessage4MQDTO messageForwarding) {
        return send4ForwardingMessage(forwardingTopic, messageForwarding);
    }

    /**
     * 将消息发送的待转发队列，主要用于转发失败重试
     *
     * @param messageForwardings 消息转发明细集合
     * @return 发送结果：true|false
     */
    public boolean send4Fowarding(List<ForwardingMessage4MQDTO> messageForwardings) {
        return send4ForwardingMessage(forwardingTopic, messageForwardings);
    }

    /**
     * 将消息发送的待回调队列，主要用于回调失败重试
     *
     * @param messageForwarding 消息字符串
     * @return 发送结果：true|false
     */
    public boolean send4Callback(ForwardingMessage4MQDTO messageForwarding) {
        return send4ForwardingMessage(callbackTopic, messageForwarding);
    }

    /**
     * 将消息发送的待回调队列，主要用于回调失败重试
     *
     * @param messageForwardings 消息字符串集合
     * @return 发送结果：true|false
     */
    public boolean send4Callback(List<ForwardingMessage4MQDTO> messageForwardings) {
        return send4ForwardingMessage(callbackTopic, messageForwardings);
    }

    /**
     * 将消息发送的给定的队列
     *
     * @param destination 给定的队列，组成形式：topic:tag
     * @param message     消息字符串
     * @param keys        消息ID
     * @return 发送结果：true|false
     */
    public boolean send(String destination, String message, String keys) {
        if (StringUtil.isBlank(destination)) {
            throw new CustomBusinessException(ResultStatuses.MF_1006);
        }
        if (StringUtil.isBlank(message)) {
            throw new CustomBusinessException(ResultStatuses.MF_1007);
        }
        try {
            Message<String> springMessage = MessageBuilder.withPayload(message)
                    .setHeader(MessageConst.PROPERTY_KEYS, keys).build();
            SendResult sendResult = rocketMQTemplate.syncSend(destination, springMessage);
            LOGGER.info("消息发送结果：{}", sendResult);
            return true;
        } catch (Exception e) {
            throw new CustomBusinessException(ResultStatuses.MF_1008, e);
        }
    }

    /**
     * 批量发送消息
     *
     * @param destination 给定的队列，组成形式：topic:tag
     * @param messages    消息字符串集合
     * @return 发送结果：true|false
     */
    public boolean send(String destination, List<Message<String>> messages) {
        if (StringUtil.isBlank(destination)) {
            throw new CustomBusinessException(ResultStatuses.MF_1006);
        }
        if (Objects.isNull(messages) || messages.isEmpty()) {
            throw new CustomBusinessException(ResultStatuses.MF_1007);
        }
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(destination, messages);
            LOGGER.info("消息发送结果：{}", sendResult);
            return true;
        } catch (Exception e) {
            throw new CustomBusinessException(ResultStatuses.MF_1008, e);
        }
    }

    /**
     * 发送消息
     *
     * @param destination       给定的队列，组成形式：topic:tag
     * @param messageForwarding 转发明细
     * @return 发送结果：true|false
     */
    private boolean send4ForwardingMessage(String destination, ForwardingMessage4MQDTO messageForwarding) {
        if (StringUtil.isBlank(destination)) {
            throw new CustomBusinessException(ResultStatuses.MF_1006);
        }
        if (Objects.isNull(messageForwarding)) {
            throw new CustomBusinessException(ResultStatuses.MF_1007);
        }
        String keys = messageForwarding.getMessageNo() + "-" + messageForwarding.getForwardingId();
        return send(destination, JsonUtil.object2Json(messageForwarding), keys);
    }

    /**
     * 发送消息
     *
     * @param destination        给定的队列，组成形式：topic:tag
     * @param messageForwardings 转发明细集合
     * @return 发送结果：true|false
     */
    private boolean send4ForwardingMessage(String destination, List<ForwardingMessage4MQDTO> messageForwardings) {
        if (StringUtil.isBlank(destination)) {
            throw new CustomBusinessException(ResultStatuses.MF_1006);
        }
        if (Objects.isNull(messageForwardings) || messageForwardings.isEmpty()) {
            throw new CustomBusinessException(ResultStatuses.MF_1007);
        }
        List<Message<String>> messages = messageForwardings.parallelStream()
                .map(e -> MessageBuilder.withPayload(JsonUtil.object2Json(e))
                        .setHeader(MessageConst.PROPERTY_KEYS, e.getMessageNo() + "-" + e.getForwardingId()).build())
                .collect(Collectors.toList());
        return send(destination, messages);
    }
}
