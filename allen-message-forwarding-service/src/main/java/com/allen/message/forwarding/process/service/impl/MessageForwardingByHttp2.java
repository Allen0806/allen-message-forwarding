package com.allen.message.forwarding.process.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;

/**
 * 通过HTTP转发消息实现类
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Service("messageForwardingByHttp2")
public class MessageForwardingByHttp2 implements MessageForwarding {
	
	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardingByHttp.class);

	/**
	 * 消息管理服务
	 */
	@Autowired
	private MessageManagementService messageManagementService;

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	@Override
	public void forward(MessageForwardingDTO messageForwardingDTO) {

	}
}
