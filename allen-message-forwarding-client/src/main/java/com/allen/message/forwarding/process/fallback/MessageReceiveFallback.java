package com.allen.message.forwarding.process.fallback;

import org.springframework.stereotype.Component;

import com.allen.message.forwarding.process.feign.MessageReceiveClient;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.tool.result.BaseResult;
import com.allen.tool.result.ResultStatus;

/**
 * Hystrix限流处理类
 * 
 * @author Allen
 * @date 2020年4月20日
 */
@Component
public class MessageReceiveFallback implements MessageReceiveClient {

	@Override
	public BaseResult<Object> receive(MessageDTO messageDTO) {
		return new BaseResult<Object>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

}