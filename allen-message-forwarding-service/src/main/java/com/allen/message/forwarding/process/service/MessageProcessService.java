package com.allen.message.forwarding.process.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.allen.message.forwarding.process.model.MessageForwarding4MQ;
import com.allen.message.forwarding.process.model.MessageSendingDTO;

/**
 * 消息转发处理服务层接口
 * 
 * @author Allen
 * @date 2020年11月5日
 * @since 1.0.0
 */
@Validated
public interface MessageProcessService {

	/**
	 * 接收上游系统发送过来的消息，保存到数据库，并发送的消息队列进行下一步转发处理
	 * 
	 * @param messageReceiveDTO 消息传入对象
	 */
	void send(@NotNull(message = "消息传入对象不能为空") @Valid MessageSendingDTO messageReceiveDTO);

	/**
	 * 转发消息
	 * 
	 * @param messageForwarding 消息转发明细
	 */
	void forward(@NotNull(message = "消息转发明细不能为空") @Valid MessageForwarding4MQ messageForwarding);

	/**
	 * 转发结果回调
	 * 
	 * @param messageForwarding 消息转发明细
	 */
	void callback(@NotNull(message = "消息转发明细不能为空") @Valid MessageForwarding4MQ messageForwarding);
}
