package com.allen.message.forwarding.process.service.impl;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.result.BaseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过HTTP转发消息实现类
 *
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@RefreshScope
@Service("messageForwardingByHttp")
public class MessageForwardingByHttp implements MessageForwarding {

    /**
     * 日志纪录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardingByHttp.class);

    /**
     * 消息管理服务
     */
    @Resource
    private MessageManagementService messageManagementService;

    /**
     * 消息处理服务
     */
    @Resource
    private MessageProcessService messageProcessService;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public void forward(MessageForwardingDTO messageForwardingDTO) {
        boolean forwardingResult = false;
        MessageDTO messageDTO = messageManagementService.getMessage(messageForwardingDTO.getMessageNo());
        try {
            String messageContent = messageDTO.getMessageContent();
            Map<String, String> httpHeaderMap = messageDTO.getHttpHeaders();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType("application/json;charset=UTF-8"));
            if (httpHeaderMap != null && !httpHeaderMap.isEmpty()) {
                for (Map.Entry<String, String> entry : httpHeaderMap.entrySet()) {
                    httpHeaders.add(entry.getKey(), entry.getValue());
                }
            }
            Map<String, String> httpParam = new HashMap<>();
            httpParam.put("message", messageContent);
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(httpParam, httpHeaders);
            ResponseEntity<BaseResult> responseEntity = restTemplate.postForEntity(messageForwardingDTO.getTargetAddress(), httpEntity, BaseResult.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
                LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo());
            } else {
                BaseResult<?> baseResult = responseEntity.getBody();
                forwardingResult = baseResult.isSuccessful();
            }
        } catch (Exception e) {
            LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo(), e);
        }
        messageProcessService.updateForwardingResult(messageForwardingDTO, forwardingResult, Boolean.TRUE);
    }
}
