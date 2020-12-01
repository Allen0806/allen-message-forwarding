package com.allen.message.forwarding.process.service.impl;

import org.springframework.stereotype.Service;

import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageForwarding;

/**
 * 通过HTTP转发消息实现类
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Service("messageForwardingByHttp2")
public class MessageForwardingByHttp2 implements MessageForwarding {

	@Override
	public void forward(MessageForwardingDTO messageForwardingDTO) {

	}
}
