package com.allen.message.forwarding.process.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageForwardingQueryParamDTO;
import com.allen.message.forwarding.process.model.MessageQueryParamDTO;
import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageManagementService;
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
@RequestMapping(path = "/mf/process/")
public class MessageProcessController {

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	/**
	 * 消息管理服务
	 */
	@Autowired
	private MessageManagementService messageManagementService;

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

	/**
	 * 获取消息信息，同时返回消息转发明细
	 * 
	 * @param messageNo 消息流水号
	 * @return 消息信息
	 */
	@PostMapping(value = "/get/message/{messageNo}")
	BaseResult<MessageDTO> getMessage(@NotNull(message = "消息流水号不能为空") @PathVariable("messageNo") String messageNo) {
		MessageDTO messageDTO = messageManagementService.getMessage(messageNo);
		if (messageDTO != null) {
			MessageForwardingQueryParamDTO forwardingQueryParam = new MessageForwardingQueryParamDTO();
			forwardingQueryParam.setMessageNo(messageNo);
			List<MessageForwardingDTO> messageForwardings = messageManagementService
					.listMessageForwarding(forwardingQueryParam);
			messageDTO.setMessageForwardings(messageForwardings);
		}
		return BaseResult.success(messageDTO);
	}

	/**
	 * 查询符合条件的消息数量
	 * 
	 * @param messageQueryParam 查询条件
	 * @return 消息数量
	 */
	@PostMapping(value = "/count/message")
	BaseResult<Integer> countMessage(
			@NotNull(message = "消息查询条件不能为空") @Valid @RequestBody MessageQueryParamDTO messageQueryParam) {
		Integer count = messageManagementService.countMessage(messageQueryParam);
		return BaseResult.success(count);
	}

	/**
	 * 根据查询条件查询消息信息，不包含转发明细信息
	 * 
	 * @param messageQueryParam 查询条件
	 * @return 消息列表
	 */
	@PostMapping(value = "/list/message")
	BaseResult<List<MessageDTO>> listMessage(
			@NotNull(message = "消息查询条件不能为空") @Valid @RequestBody MessageQueryParamDTO messageQueryParam) {
		List<MessageDTO> messages = messageManagementService.listMessage(messageQueryParam);
		return BaseResult.success(messages);
	}

	/**
	 * 获取消息转发明细
	 * 
	 * @param messageNo    消息流水号
	 * @param forwardingId 转发配置主键
	 * @return 息转发明细信息
	 */
	@PostMapping(value = "/get/forwarding/{messageNo}/{forwardingId}")
	BaseResult<MessageForwardingDTO> getMessageForwarding(
			@NotNull(message = "消息流水号不能为空") @PathVariable("messageNo") String messageNo,
			@NotNull(message = "转发配置ID不能为空") @PathVariable("forwardingId") Long forwardingId) {
		MessageForwardingDTO forwarding = messageManagementService.getMessageForwarding(messageNo, forwardingId);
		return BaseResult.success(forwarding);
	}

	/**
	 * 查询符合条件的消息转发明细数量
	 * 
	 * @param forwardingQueryParam 查询条件
	 * @return 数量
	 */
	@PostMapping(value = "/count/forwarding")
	BaseResult<Integer> countMessageForwarding(
			@NotNull(message = "消息转发明细查询条件不能为空") @Valid @RequestBody MessageForwardingQueryParamDTO forwardingQueryParam) {
		Integer count = messageManagementService.countMessageForwarding(forwardingQueryParam);
		return BaseResult.success(count);
	}

	/**
	 * 根据查询条件查询消息转发信息
	 * 
	 * @param forwardingQueryParam 查询条件
	 * @return 消息转发信息列表
	 */
	@PostMapping(value = "/list/forwarding")
	BaseResult<List<MessageForwardingDTO>> listMessageForwarding(
			@NotNull(message = "消息转发明细查询条件不能为空") @Valid @RequestBody MessageForwardingQueryParamDTO forwardingQueryParam) {
		List<MessageForwardingDTO> forwardings = messageManagementService.listMessageForwarding(forwardingQueryParam);
		return BaseResult.success(forwardings);
	}
}
