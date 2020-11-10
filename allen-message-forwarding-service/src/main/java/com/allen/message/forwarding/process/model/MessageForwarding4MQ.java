package com.allen.message.forwarding.process.model;

import java.io.Serializable;

import com.allen.message.forwarding.constant.ForwardingWay;

/**
 * 用于MQ的消息转发明细信息
 * 
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
public class MessageForwarding4MQ implements Serializable {

	/**
	 * 序列化版本
	 */
	private static final long serialVersionUID = 3470662323976989293L;

	/**
	 * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
	 */
	private String messageNo;

	/**
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	private Integer messageId;

	/**
	 * 消息转发配置主键
	 */
	private Long forwardingId;

	/**
	 * 来源系统ID，固定4位
	 */
	private Integer sourceSystemId;

	/**
	 * 目标系统名称，最长30位，新增时不可为空
	 */
	private String targetSystem;

	/**
	 * 转发到目标系统的方式，固定2位长度：01-Http，02-Kafka，03-RocketMQ，新增时不能为空
	 */
	private ForwardingWay forwardingWay;

	/**
	 * 目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔），新增时不可为空
	 */
	private String targetAddress;

	/**
	 * 最大重试次数，默认值为3，最大值为10次
	 */
	private Integer maxRetryTimes;

	/**
	 * 是否需要回调，0-否，1-是，默认为0，回调重试次数固定为3
	 */
	private Integer callbackRequired;

	/**
	 * 消息发送结果回调地址，最长200位
	 */
	private String callbackUrl;

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

	public Integer getSourceSystemId() {
		return sourceSystemId;
	}

	public void setSourceSystemId(Integer sourceSystemId) {
		this.sourceSystemId = sourceSystemId;
	}

	public String getTargetSystem() {
		return targetSystem;
	}

	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
	}

	public ForwardingWay getForwardingWay() {
		return forwardingWay;
	}

	public void setForwardingWay(ForwardingWay forwardingWay) {
		this.forwardingWay = forwardingWay;
	}

	public String getTargetAddress() {
		return targetAddress;
	}

	public void setTargetAddress(String targetAddress) {
		this.targetAddress = targetAddress;
	}

	public Integer getMaxRetryTimes() {
		return maxRetryTimes;
	}

	public void setMaxRetryTimes(Integer maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}

	public Integer getCallbackRequired() {
		return callbackRequired;
	}

	public void setCallbackRequired(Integer callbackRequired) {
		this.callbackRequired = callbackRequired;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("MessageForwarding4MQ[").append("messageNo=").append(messageNo)
				.append(", messageId=").append(messageId).append(", forwardingId=").append(forwardingId)
				.append(", sourceSystemId=").append(sourceSystemId).append(", targetSystem=").append(targetSystem)
				.append(", forwardingWay=").append(forwardingWay).append(", targetAddress=").append(targetAddress)
				.append(", maxRetryTimes=").append(maxRetryTimes).append(", callbackRequired=").append(callbackRequired)
				.append(", callbackUrl=").append(callbackUrl).append("]").toString();
	}
}
