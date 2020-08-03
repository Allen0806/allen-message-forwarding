package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消息转发配置信息传输类
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@ApiModel("消息转发配置信息传输类")
public class MessageForwardingConfigDTO implements Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 2334563804173313153L;

	/**
	 * 消息ID
	 */
	@ApiModelProperty(value = "消息ID", dataType = "Long")
	private String messageId;

	/**
	 * 目标系统名称，最长30位
	 */
	@ApiModelProperty(value = "目标系统名称，最长30位，新增时不可为空", dataType = "String")
	private String targetSystem;

	/**
	 * 转发到目标系统的方式，固定2位长度：01-Http，02-Kafka，03-RocketMQ
	 */
	@ApiModelProperty(value = "转发到目标系统的方式，固定2位长度：01-Http，02-Kafka，03-RocketMQ", dataType = "String")
	private String forwardingWay;

	/**
	 * 目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔）
	 */
	@ApiModelProperty(value = "目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔）", dataType = "String")
	private String targetAddress;

	/**
	 * 重试次数，默认值为3，最大值为10次
	 */
	@ApiModelProperty(value = "重试次数，默认值为3，最大值为10次", dataType = "Integer")
	private Integer retryTimes;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTargetSystem() {
		return targetSystem;
	}

	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
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

	public Integer getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(Integer retryTimes) {
		this.retryTimes = retryTimes;
	}

	@Override
	public String toString() {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append("MessageForwardingConfigDTO[").append(", messageId=").append(messageId)
				.append(", targetSystem=").append(targetSystem).append(", forwardingWay=").append(forwardingWay)
				.append(", targetAddress=").append(targetAddress).append(", retryTimes=").append(retryTimes)
				.append("]");
		return sbuilder.toString();
	}
}