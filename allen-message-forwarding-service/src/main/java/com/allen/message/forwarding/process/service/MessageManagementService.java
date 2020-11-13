package com.allen.message.forwarding.process.service;

import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
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
	
	/**
	 * 更新消息信息
	 * 
	 * @param messageDTO 消息信息
	 */
	void updateMessage(MessageDTO messageDTO);

	/**
	 * 同时更新消息及转发明细信息
	 * 
	 * @param messageDTO           消息信息
	 * @param messageForwardingDTO 消息转发明细信息
	 */
	void updateForwardingResult(MessageDTO messageDTO, MessageForwardingDTO messageForwardingDTO);

	/**
	 * 更新消息转发明细，更新时只给定主键、上次更新时间及需要更新的属性即可，不更新的属性不要给定
	 * 
	 * @param messageForwardingDTO 消息转发明细信息
	 */
	void updateForwardingResult(MessageForwardingDTO messageForwardingDTO);

	/**
	 * 更新转发明细回调结果
	 * 
	 * @param messageForwardingDTO 消息转发明细信息
	 */
	void updateCallbackResult(MessageForwardingDTO messageForwardingDTO);

	/**
	 * 获取消息信息
	 * 
	 * @param messageNo 消息流水号
	 * @return 消息信息
	 */
	MessageDTO getMessage(String messageNo);

	/**
	 * 获取消息转发明细
	 * 
	 * @param messageNo    消息流水号
	 * @param forwardingId 转发配置主键
	 * @return 息转发明细信息
	 */
	MessageForwardingDTO getMessageForwarding(String messageNo, Long forwardingId);
}
