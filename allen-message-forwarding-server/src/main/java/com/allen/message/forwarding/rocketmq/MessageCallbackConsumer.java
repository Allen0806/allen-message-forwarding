package com.allen.message.forwarding.rocketmq;

import com.allen.message.forwarding.process.model.ForwardingMessage4MQDTO;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.string.StringUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * 转发结果回调消息消费者
 *
 * @author Allen
 * @date 2020年11月11日
 * @since 1.0.0
 */
@Service
@RefreshScope
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = "${allen.message.forwarding.rocketmq.callback.topic}", consumerGroup = "${allen.message.forwarding.rocketmq.callback.consumer.group}")
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
            ForwardingMessage4MQDTO messageForwarding4MQ = JsonUtil.json2Object(message, ForwardingMessage4MQDTO.class);
            messageProcessService.callback(messageForwarding4MQ);
        } catch (Exception e) {
            LOGGER.info("消费消息转发明细结果回调出错，消息转发明细信息：" + message, e);
        }

    }
}
