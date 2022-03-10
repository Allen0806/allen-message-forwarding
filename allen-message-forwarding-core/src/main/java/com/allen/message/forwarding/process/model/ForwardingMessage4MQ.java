package com.allen.message.forwarding.process.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

/**
 * 用于MQ的消息转发明细信息
 * 
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
public class ForwardingMessage4MQ implements Serializable {

	/**
	 * 序列化版本
	 */
	private static final long serialVersionUID = 3470662323976989293L;

	/**
	 * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
	 */
	@NotNull(message = "消息流水号不能为空")
	@Size(min = 32, max = 32, message = "消息流水号固定32位")
	private String messageNo;

	/**
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	@NotNull(message = "消息ID不能为空")
	@Range(min = 100000, max = 999999, message = "消息ID取值范围为100000~999999")
	private Integer messageId;

	/**
	 * 消息转发配置主键
	 */
	@NotNull(message = "消息转发配置主键不能为空")
	private Long forwardingId;

	public String getMessageNo() {
		return messageNo;
	}

	public void setMessageNo(String messageNo) {
		this.messageNo = messageNo;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Long getForwardingId() {
		return forwardingId;
	}

	public void setForwardingId(Long forwardingId) {
		this.forwardingId = forwardingId;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("MessageForwarding4MQ[").append("messageNo=").append(messageNo)
				.append(", messageId=").append(messageId).append(", forwardingId=").append(forwardingId).append("]")
				.toString();
	}
}
