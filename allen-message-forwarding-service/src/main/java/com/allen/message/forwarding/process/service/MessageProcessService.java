package com.allen.message.forwarding.process.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.allen.message.forwarding.process.model.ForwardingMessage4MQ;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
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
	 * @param messageSendingDTO 消息传入对象
	 */
	void send(@NotNull(message = "消息传入对象不能为空") @Valid MessageSendingDTO messageSendingDTO);

	/**
	 * 转发消息
	 * 
	 * @param messageForwarding 消息转发明细
	 */
	void forward(@NotNull(message = "消息转发明细不能为空") @Valid ForwardingMessage4MQ messageForwarding);

	/**
	 * 更新转发结果
	 * 
	 * @param messageForwardingDTO 转发明细
	 * @param forwardingResult     转发结果
	 * @param needRetry            是否需要重试
	 */
	void updateForwardingResult(MessageForwardingDTO messageForwardingDTO, boolean forwardingResult, boolean needRetry);

	/**
	 * 转发结果回调
	 * 
	 * @param messageForwarding 消息转发明细
	 */
	void callback(@NotNull(message = "消息转发明细不能为空") @Valid ForwardingMessage4MQ messageForwarding);

	/**
	 * 更新回调结果
	 * 
	 * @param messageForwardingDTO 转发明细
	 * @param callbackResult       回调结果
	 * @param needRetry            是否需要重试
	 */
	void updateCallbackResult(MessageForwardingDTO messageForwardingDTO, boolean callbackResult, boolean needRetry);

	/**
	 * 转发重试
	 */
	void retryForward();

	/**
	 * 回调重试
	 */
	void retryCallback();

	/**
	 * 历史消息迁移
	 */
	void migrate();
}
