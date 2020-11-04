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
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	private Integer messageId;

	/**
	 * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
	 */
	private String messageKeyword;

	/**
	 * 消息转发配置主键
	 */
	private Long forwardingId;

	/**
	 * 转发结果：0-失败，1-成功
	 */
	private Integer forwardingResult;

	/**
	 * 转发成功时间
	 */
	private LocalDateTime forwardingSucessTime;

	/**
	 * 重试次数
	 */
	private Integer retryTimes;

	/**
	 * 回调结果：0-失败，1-成功
	 */
	private Integer callbackResult;

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

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public String getMessageKeyword() {
		return messageKeyword;
	}

	public void setMessageKeyword(String messageKeyword) {
		this.messageKeyword = messageKeyword;
	}

	public Long getForwardingId() {
		return forwardingId;
	}

	public void setForwardingId(Long forwardingId) {
		this.forwardingId = forwardingId;
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

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	public Integer getCallbackResult() {
		return callbackResult;
	}

	public void setCallbackResult(Integer callbackResult) {
		this.callbackResult = callbackResult;
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
				.append(messageNo).append(", messageId=").append(messageId).append(", messageKeyword=")
				.append(messageKeyword).append(", forwardingId=").append(forwardingId).append(", forwardingResult=")
				.append(forwardingResult).append(", forwardingSucessTime=").append(forwardingSucessTime)
				.append(", retryTimes=").append(retryTimes).append(", callbackResult=").append(callbackResult)
				.append(", callbackRetryTimes=").append(callbackRetryTimes).append(", createTime=").append(createTime)
				.append(", updateTime=").append(updateTime).append("]").toString();
	}
}
