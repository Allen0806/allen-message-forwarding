package com.allen.message.forwarding.process.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.result.BaseResult;

/**
 * 消息处理Controller层
 * 
 * @author Allen
 * @date 2020年5月12日
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping(path = "/mf/process")
public class MessageSendController {

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	/**
	 * 接收消息
	 * 
	 * @param message 消息对象
	 * @return 响应对象
	 */
	@PostMapping(value = "/send")
	public BaseResult<Object> send(@NotNull(message = "消息不能为空") @Valid @RequestBody MessageSendingDTO message) {
		messageProcessService.send(message);
		return BaseResult.success();
	}
}
