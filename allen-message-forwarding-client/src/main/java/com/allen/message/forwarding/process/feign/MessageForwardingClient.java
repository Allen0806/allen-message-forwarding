package com.allen.message.forwarding.process.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.allen.message.forwarding.process.fallback.MessageForwardingFallback;
import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.tool.result.BaseResult;

/**
 * 消息接收 Feign Client，供外部系统集成使用
 * 
 * @author Allen
 * @date 2020年4月20日
 */
@FeignClient(name = "allen-message-forwarding-server", path = "/mf", fallback = MessageForwardingFallback.class)
public interface MessageForwardingClient {

	/**
	 * 消息发送Feign Client接口
	 * 
	 * @param message 消息对象
	 * @return 接收结果
	 */
	@PostMapping(value = "/process/receive", headers = { "Content-Type=application/json" })
	public BaseResult<Object> send(@RequestBody MessageSendingDTO message);

}