package com.allen.message.forwarding.process.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息转发结果DO类
 * 
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
public class AmfMessageForwardingDO implements Serializable {

	/**
	 * 序列化版本
	 */
	private static final long serialVersionUID = -7120077672354300298L;

	/**
	 * 主键ID，修改时不可为空
	 */
	private Long id;

	/**
	 * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
	 */
	private String messageNo;

	/**
	 * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
	 */
	private String messageKeyword;

	/**
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	private Integer messageId;

	/**
	 * 消息转发配置主键
	 */
	private Long forwardingId;

	/**
	 * 转发到目标系统的方式，固定2位长度：01-Http，02-Kafka，03-RocketMQ，新增时不能为空
	 */
	private String forwardingWay;

	/**
	 * 目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔），新增时不可为空
	 */
	private String targetAddress;

	/**
	 * 最大重试次数，同时适用于转发重试及回调重试，默认值为3，最大值为10次
	 */
	private Integer maxRetryTimes;

	/**
	 * 是否需要回调，0-否，1-是
	 */
	private Integer callbackRequired;

	/**
	 * 消息发送结果回调地址，最长200位
	 */
	private String callbackUrl;

	/**
	 * 转发处理状态：0-初始化，1-处理中，2-完成
	 */
	private Integer forwardingStatus;

	/**
	 * 转发结果：0-失败，1-成功
	 */
	private Integer forwardingResult;

	/**
	 * 转发成功时间
	 */
	private LocalDateTime forwardingSucessTime;

	/**
	 * 转发重试次数
	 */
	private Integer forwardingRetryTimes;

	/**
	 * 回调处理状态：0-初始化，1-处理中，2-完成
	 */
	private Integer callbackStatus;

	/**
	 * 回调结果：0-失败，1-成功
	 */
	private Integer callbackResult;

	/**
	 * 回调成功时间
	 */
	private LocalDateTime callbackSucessTime;

	/**
	 * 回调重试次数
	 */
	private Integer callbackRetryTimes;

	/**
	 * 创建时间，默认值为系统当前时间，不可修改
	 */
	private LocalDateTime createTime;

	/**
	 * 最后修改时间，默认值为系统当前时间，数据修改时自动更新
	 */
	private LocalDateTime updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageNo() {
		return messageNo;
	}

	public void setMessageNo(String messageNo) {
		this.messageNo = messageNo;
	}

	public String getMessageKeyword() {
		return messageKeyword;
	}

	public void setMessageKeyword(String messageKeyword) {
		this.messageKeyword = messageKeyword;
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

	public String getForwardingWay() {
		return forwardingWay;
	}

	public void setForwardingWay(String forwardingWay) {
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

	public Integer getForwardingStatus() {
		return forwardingStatus;
	}

	public void setForwardingStatus(Integer forwardingStatus) {
		this.forwardingStatus = forwardingStatus;
	}

	public Integer getForwardingResult() {
		return forwardingResult;
	}

	public void setForwardingResult(Integer forwardingResult) {
		this.forwardingResult = forwardingResult;
	}

	public LocalDateTime getForwardingSucessTime() {
		return forwardingSucessTime;
	}

	public void setForwardingSucessTime(LocalDateTime forwardingSucessTime) {
		this.forwardingSucessTime = forwardingSucessTime;
	}

	public Integer getForwardingRetryTimes() {
		return forwardingRetryTimes;
	}

	public void setForwardingRetryTimes(Integer forwardingRetryTimes) {
		this.forwardingRetryTimes = forwardingRetryTimes;
	}

	public Integer getCallbackStatus() {
		return callbackStatus;
	}

	public void setCallbackStatus(Integer callbackStatus) {
		this.callbackStatus = callbackStatus;
	}

	public Integer getCallbackResult() {
		return callbackResult;
	}

	public void setCallbackResult(Integer callbackResult) {
		this.callbackResult = callbackResult;
	}

	public LocalDateTime getCallbackSucessTime() {
		return callbackSucessTime;
	}

	public void setCallbackSucessTime(LocalDateTime callbackSucessTime) {
		this.callbackSucessTime = callbackSucessTime;
	}

	public Integer getCallbackRetryTimes() {
		return callbackRetryTimes;
	}

	public void setCallbackRetryTimes(Integer callbackRetryTimes) {
		this.callbackRetryTimes = callbackRetryTimes;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("AmfMessageForwardingDO[").append("id=").append(id).append("messageNo=")
				.append(messageNo).append(", messageKeyword=").append(messageKeyword).append(", messageId=")
				.append(messageId).append(", forwardingId=").append(forwardingId).append(", forwardingWay=")
				.append(forwardingWay).append(", targetAddress=").append(targetAddress).append(", maxRetryTimes=")
				.append(maxRetryTimes).append(", callbackRequired=").append(callbackRequired).append(", callbackUrl=")
				.append(callbackUrl).append(", forwardingStatus=").append(forwardingStatus)
				.append(", forwardingResult=").append(forwardingResult).append(", forwardingSucessTime=")
				.append(forwardingSucessTime).append(", forwardingRetryTimes=").append(forwardingRetryTimes)
				.append(", callbackStatus=").append(callbackStatus).append(", callbackSucessTime=")
				.append(callbackSucessTime).append(", callbackRetryTimes=").append(callbackRetryTimes)
				.append(", createTime=").append(createTime).append(", updateTime=").append(updateTime).append("]")
				.toString();
	}
}
