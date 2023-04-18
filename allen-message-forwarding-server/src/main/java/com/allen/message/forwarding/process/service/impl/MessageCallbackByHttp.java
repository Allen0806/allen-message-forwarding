package com.allen.message.forwarding.process.service.impl;

import com.allen.message.forwarding.process.model.ForwardingMessage4CallbackDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageCallback;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.result.BaseResult;
import com.allen.tool.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 通过HTTP方式实现消息回调
 *
 * @author Allen
 * @date 2020年12月2日
 * @since 1.0.0
 */
@Service("messageCallbackByHttp")
public class MessageCallbackByHttp implements MessageCallback {

    /**
     * 日志纪录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallbackByHttp.class);

    /**
     * 消息处理服务
     */
    @Autowired
    private MessageProcessService messageProcessService;

    /**
     * restTemplate 实例
     */
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void callback(MessageForwardingDTO messageForwardingDTO) {
        String callbackUrl = messageForwardingDTO.getCallbackUrl();
        if (StringUtil.isBlank(callbackUrl)) {
            LOGGER.error("回调URL为空，转发明细信息：{}", messageForwardingDTO);
            messageProcessService.updateCallbackResult(messageForwardingDTO, false, false);
            return;
        }
        ForwardingMessage4CallbackDTO forwardingMessage4CallbackDTO = new ForwardingMessage4CallbackDTO();
        forwardingMessage4CallbackDTO.setMessageNo(messageForwardingDTO.getMessageNo());
        forwardingMessage4CallbackDTO.setMessageKeyword(messageForwardingDTO.getMessageKeyword());
        forwardingMessage4CallbackDTO.setMessageId(messageForwardingDTO.getMessageId());
        forwardingMessage4CallbackDTO.setForwardingId(messageForwardingDTO.getForwardingId());
        forwardingMessage4CallbackDTO.setForwardingResult(messageForwardingDTO.getForwardingResult());
        boolean callbackResult = false;
        try {
            BaseResult<?> baseResult = restTemplate.postForObject(callbackUrl,
                    JsonUtil.object2Json(forwardingMessage4CallbackDTO), BaseResult.class);
            callbackResult = baseResult.isSuccessful();
        } catch (Exception e) {
            LOGGER.error("消息回调出错，转发明细信息：" + messageForwardingDTO, e);
            callbackResult = false;
        }
        messageProcessService.updateCallbackResult(messageForwardingDTO, callbackResult, Boolean.TRUE);
    }

}
