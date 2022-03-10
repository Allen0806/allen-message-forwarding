package com.allen.message.forwarding.process.service;

import com.allen.message.forwarding.process.model.MessageForwardingDTO;

/**
 * 消息转发处理顶层接口，不通的转发方式需实现此接口
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
public interface MessageForwarding {

	/**
	 * 转发消息
	 * 
	 * @param messageForwardingDTO 消息转发明细DTO
	 */
	void forward(MessageForwardingDTO messageForwardingDTO);
}
