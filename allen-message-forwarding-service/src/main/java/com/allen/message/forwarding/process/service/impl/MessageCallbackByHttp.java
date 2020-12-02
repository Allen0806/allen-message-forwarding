package com.allen.message.forwarding.process.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageCallback;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;

/**
 * 通过HTTP方式实现消息回调
 * 
 * @author Allen
 * @date 2020年12月2日
 * @since 1.0.0
 */
public class MessageCallbackByHttp implements MessageCallback {
	
	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallbackByHttp.class);

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
	public void callback(MessageForwardingDTO messageForwardingDTO) {
		// TODO Auto-generated method stub

	}

}
