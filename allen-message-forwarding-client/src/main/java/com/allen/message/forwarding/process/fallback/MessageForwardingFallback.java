package com.allen.message.forwarding.process.fallback;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

import com.allen.message.forwarding.process.feign.MessageForwardingClient;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageForwardingQueryParamDTO;
import com.allen.message.forwarding.process.model.MessageQueryParamDTO;
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
public class MessageForwardingFallback implements MessageForwardingClient {

	@Override
	public BaseResult<Object> send(MessageSendingDTO messageDTO) {
		return new BaseResult<Object>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

	@Override
	public BaseResult<MessageDTO> getMessage(String messageNo) {
		return new BaseResult<MessageDTO>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

	@Override
	public BaseResult<Integer> countMessage(MessageQueryParamDTO messageQueryParam) {
		return new BaseResult<Integer>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

	@Override
	public BaseResult<List<MessageDTO>> listMessage(MessageQueryParamDTO messageQueryParam) {
		return new BaseResult<List<MessageDTO>>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

	@Override
	public BaseResult<MessageForwardingDTO> getMessageForwarding(String messageNo, Long forwardingId) {
		return new BaseResult<MessageForwardingDTO>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

	@Override
	public BaseResult<Integer> countMessageForwarding(
			@NotNull(message = "消息转发明细查询条件不能为空") @Valid MessageForwardingQueryParamDTO forwardingQueryParam) {
		return new BaseResult<Integer>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

	@Override
	public BaseResult<List<MessageForwardingDTO>> listMessageForwarding(
			@NotNull(message = "消息转发明细查询条件不能为空") @Valid MessageForwardingQueryParamDTO forwardingQueryParam) {
		return new BaseResult<List<MessageForwardingDTO>>(ResultStatus.SYSTEM_ERROR.getCode(), "请求失败");
	}

}