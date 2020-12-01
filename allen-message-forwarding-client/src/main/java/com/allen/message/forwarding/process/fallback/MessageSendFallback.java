package com.allen.message.forwarding.process.fallback;

import org.springframework.stereotype.Component;

import com.allen.message.forwarding.process.feign.MessageSendClient;
import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.tool.result.BaseResult;
import com.allen.tool.result.ResultStatus;

/**
 * Hystrix限流处理类
 * 
 * @author Allen
 * @date 2020年4月20日
 */
@Component
public class MessageSendFallback implements MessageSendClient {

	@Override
	public BaseResult<Object> send(MessageSendingDTO messageDTO) {
		return new BaseResult<Object>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}
}