package com.allen.mf.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.allen.mf.dto.MessageDTO;
import com.allen.mf.fallback.MessageReceiveFallback;
import com.allen.tool.result.BaseResult;

/**
 * 
 * 
 * @author Allen
 * @date 2020年4月20日
 */
@FeignClient(name = "allen-message-forwarding-service", path = "/mmf", fallback = MessageReceiveFallback.class)
public interface MessageReceiveClient {

	/**
	 * 新增操作.
	 * 
	 * @param request 请求对象
	 * @return 响应对象
	 */
	@PostMapping(value = "/process/message/receive", headers = { "Content-Type=application/json" })
	public BaseResult<Object> receive(@RequestBody MessageDTO request);

}