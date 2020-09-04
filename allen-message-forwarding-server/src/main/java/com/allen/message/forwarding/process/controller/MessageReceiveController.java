package com.allen.message.forwarding.process.controller;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.tool.result.BaseResult;

/**
 * 消息接收Controller层，用于接收通过http接口或未付
 * 
 * @author Allen
 * @date 2020年5月12日
 * @since 1.0.0
 *
 */
@RestController
@RequestMapping(path = "/message/forwarding")
public class MessageReceiveController {

	/**
	 * 接收消息
	 * 
	 * @param messageDTO 消息对象
	 * @return 响应对象
	 */
	@PostMapping(value = "/process/receive")
	public BaseResult<Object> receive(@RequestBody @Valid MessageDTO messageDTO, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return BaseResult.paramError(bindingResult.getAllErrors());
		}
		return BaseResult.success();
	}
}
