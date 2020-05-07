package com.allen.mf.fallback;

import org.springframework.stereotype.Component;

import com.allen.mf.dto.MessageDTO;
import com.allen.mf.feign.MessageReceiveClient;
import com.allen.tool.result.BaseResult;

/**
 * 
 * 
 * @author Allen
 * @date 2020年4月20日
 */
@Component
public class MessageReceiveFallback implements MessageReceiveClient {

	@Override
	public BaseResult<Object> receive(MessageDTO request) {
		return new BaseResult<Object>(BaseResult.STATUS_SYSTEM_FAILURE, "请求失败");
	}

}