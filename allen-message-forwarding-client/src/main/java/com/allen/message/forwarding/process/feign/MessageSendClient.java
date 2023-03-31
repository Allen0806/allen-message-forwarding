package com.allen.message.forwarding.process.feign;

import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.tool.result.BaseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 消息接收 Feign Client，供外部系统集成使用
 *
 * @author Allen
 * @date 2020年4月20日
 */
@FeignClient(name = "allen-message-forwarding", contextId = "messageSendClient", path = "/mf/process")
public interface MessageSendClient {

    /**
     * 消息发送Feign Client接口
     *
     * @param message 消息对象
     * @return 接收结果
     */
    @PostMapping(value = "/send", headers = {"Content-Type=application/json"})
    BaseResult<Object> send(@RequestBody MessageSendingDTO message);
}