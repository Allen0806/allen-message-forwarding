package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息转发配置信息类，用来存取消息转发配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
public class AmfMessageForwardingConfigDO implements Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 9038294298613813301L;

	/**
	 * 主键ID，修改时不可为空
	 */
	private Long id;

	/**
	 * 消息配置主键ID，新增时不可为空
	 */
	private Long messageConfigId;

	/**
	 * 目标系统名称，最长30位，新增时不可为空
	 */
	private String targetSystem;

	/**
	 * 转发到目标系统的方式，固定2位长度：01-Http，02-Kafka，03-RocketMQ，新增时不能为空
	 */
	private String forwardingWay;

	/**
	 * 目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔），新增时不可为空
	 */
	private String targetAddress;

	/**
	 * 重试次数，默认值为3，最大值为10次
	 */
	private Integer retryTimes;

	/**
	 * 是否需要回调，0-否，1-是，默认为0，回调重试次数固定为3
	 */
	private Integer callbackRequired;

	/**
	 * 是否删除标记：0-否，1-是，默认为0
	 */
	private Integer deleted;

	/**
	 * 创建人ID，最长20位，新增时不可为空，不可修改
	 */
	private String createdBy;

	/**
	 * 创建时间，默认值为系统当前时间
	 */
	private LocalDateTime createTime;

	/**
	 * 最后修改人ID，最长20位，修改时不可为空
	 */
	private String updatedBy;

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

	public Long getMessageConfigId() {
		return messageConfigId;
	}

	public void setMessageConfigId(Long messageConfigId) {
		this.messageConfigId = messageConfigId;
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

	public Integer getCallbackRequired() {
		return callbackRequired;
	}

	public void setCallbackRequired(Integer callbackRequired) {
		this.callbackRequired = callbackRequired;
	}

	public Integer getDeleted() {
		return deleted;
	}

	public void setDeleted(Integer deleted) {
		this.deleted = deleted;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		StringBuilder sbuilder = new StringBuilder();
		sbuilder.append("AmfSourceSystemConfigDO[").append("id=").append(id).append(", messageConfigId=")
				.append(messageConfigId).append(", targetSystem=").append(targetSystem).append(", forwardingWay=")
				.append(forwardingWay).append(", targetAddress=").append(targetAddress).append(", retryTimes=")
				.append(retryTimes).append(", callbackRequired=").append(callbackRequired).append(", deleted=")
				.append(deleted).append(", createdBy=").append(createdBy).append(", createTime=").append(createTime)
				.append(", updatedBy=").append(updatedBy).append(", updateTime=").append(updateTime).append("]");
		return sbuilder.toString();
	}

}
