package com.allen.message.forwarding.process.service;

import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.process.model.MessageSendingDTO;

/**
 * 消息管理服务，封装对数据库层的操作
 * 
 * @author Allen
 * @date 2020年11月6日
 * @since 1.0.0
 */
public interface MessageManagementService {

	/**
	 * 保存消息信息，包含消息明细信息
	 * 
	 * @param messageReceiveDTO 消息传入对象
	 * @param messageConfigDTO  消息配置信息
	 */
	void save(MessageSendingDTO messageReceiveDTO, MessageConfigDTO messageConfigDTO);
}
