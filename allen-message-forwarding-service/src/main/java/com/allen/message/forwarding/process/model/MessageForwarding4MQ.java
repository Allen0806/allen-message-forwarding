package com.allen.message.forwarding.process.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

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
	@NotNull(message = "转发到目标系统的方式不能为空")
	@Pattern(regexp = "^01|02|03$", message = "转发到目标系统的方式不为01、02或03之一")
	private ForwardingWay forwardingWay;

	/**
	 * 目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔），新增时不可为空
	 */
	@NotNull(message = "目标地址不能为空")
	private String targetAddress;

	/**
	 * 最大重试次数，默认值为3，最大值为10次
	 */
	@NotNull(message = "最大重试次数不能为空")
	private Integer maxRetryTimes;

	/**
	 * 是否需要回调
	 */
	@NotNull(message = "是否需要回调不能为空")
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
