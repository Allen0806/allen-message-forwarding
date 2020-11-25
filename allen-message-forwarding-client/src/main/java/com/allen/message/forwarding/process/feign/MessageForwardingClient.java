package com.allen.message.forwarding.process.feign;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.allen.message.forwarding.process.fallback.MessageForwardingFallback;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageForwardingQueryParamDTO;
import com.allen.message.forwarding.process.model.MessageQueryParamDTO;
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
	@PostMapping(value = "/process/send", headers = { "Content-Type=application/json" })
	BaseResult<Object> send(@RequestBody MessageSendingDTO message);

	/**
	 * 获取消息信息，同时返回消息转发明细
	 * 
	 * @param messageNo 消息流水号
	 * @return 消息信息
	 */
	@PostMapping(value = "/get/message/{messageNo}", headers = { "Content-Type=application/json" })
	BaseResult<MessageDTO> getMessage(@PathVariable("messageNo") String messageNo);

	/**
	 * 查询符合条件的消息数量
	 * 
	 * @param messageQueryParam 查询条件
	 * @return 消息数量
	 */
	@PostMapping(value = "/count/message", headers = { "Content-Type=application/json" })
	BaseResult<Integer> countMessage(@RequestBody MessageQueryParamDTO messageQueryParam);

	/**
	 * 根据查询条件查询消息信息，不包含转发明细信息
	 * 
	 * @param messageQueryParam 查询条件
	 * @return 消息列表
	 */
	@PostMapping(value = "/list/message", headers = { "Content-Type=application/json" })
	BaseResult<List<MessageDTO>> listMessage(@RequestBody MessageQueryParamDTO messageQueryParam);

	/**
	 * 获取消息转发明细
	 * 
	 * @param messageNo    消息流水号
	 * @param forwardingId 转发配置主键
	 * @return 息转发明细信息
	 */
	@PostMapping(value = "/get/forwarding/{messageNo}/{forwardingId}", headers = { "Content-Type=application/json" })
	BaseResult<MessageForwardingDTO> getMessageForwarding(@PathVariable("messageNo") String messageNo,
			@PathVariable("forwardingId") Long forwardingId);

	/**
	 * 查询符合条件的消息转发明细数量
	 * 
	 * @param forwardingQueryParam 查询条件
	 * @return 数量
	 */
	@PostMapping(value = "/count/forwarding")
	BaseResult<Integer> countMessageForwarding(
			@NotNull(message = "消息转发明细查询条件不能为空") @Valid @RequestBody MessageForwardingQueryParamDTO forwardingQueryParam);

	/**
	 * 根据查询条件查询消息转发信息
	 * 
	 * @param forwardingQueryParam 查询条件
	 * @return 消息转发信息列表
	 */
	@PostMapping(value = "/list/forwarding", headers = { "Content-Type=application/json" })
	BaseResult<List<MessageForwardingDTO>> listMessageForwarding(
			@NotNull(message = "消息转发明细查询条件不能为空") @Valid @RequestBody MessageForwardingQueryParamDTO forwardingQueryParam);

}