package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.allen.tool.validation.ValidationGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消息转发配置信息类，用来存取消息转发配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@ApiModel("消息转发配置信息")
public class MessageForwardingConfigVO implements Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 2188185008293925429L;

	/**
	 * 主键ID，修改时不可为空
	 */
	@ApiModelProperty(value = "主键ID，修改时不可为空", dataType = "Long", required = true)
	@NotNull(message = "主键ID不能为空", groups = { ValidationGroup.Update.class })
	private Long id;

	/**
	 * 消息ID，新增时和修改时不可为空
	 */
	@ApiModelProperty(value = "消息配置主键ID，新增时不可为空", dataType = "Integer", required = true)
	@NotNull(message = "消息ID不能为空")
	private Integer messageId;

	/**
	 * 目标系统名称，最长30位，新增时不可为空
	 */
	@ApiModelProperty(value = "目标系统名称，最长30位，新增时不可为空", dataType = "String", required = true)
	@NotNull(message = "目标系统名称不能为空", groups = { ValidationGroup.Insert.class })
	@Size(max = 30, message = "目标系统名称最长30位")
	private String targetSystem;

	/**
	 * 转发到目标系统的方式，固定2位长度：01-Http，02-Kafka，03-RocketMQ，新增时不能为空
	 */
	@ApiModelProperty(value = "转发到目标系统的方式，固定2位长度：01-Http，02-Kafka，03-RocketMQ，新增时不能为空", dataType = "String", required = true)
	@NotNull(message = "转发到目标系统的方式不能为空", groups = { ValidationGroup.Insert.class })
	@Pattern(regexp = "^01|02|03$", message = "转发到目标系统的方式不为01、02或03之一")
	private String forwardingWay;

	/**
	 * 目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔），新增时不可为空
	 */
	@ApiModelProperty(value = "目标地址：http接口地址/Kafka Topic/RocketMQ Topic:Tag（英文冒号分隔），新增时不可为空", dataType = "String", required = true)
	@NotNull(message = "目标地址不能为空", groups = { ValidationGroup.Insert.class })
	@Size(max = 200, message = "目标地址最长200位")
	private String targetAddress;

	/**
	 * 重试次数，默认值为3，最大值为10次
	 */
	@ApiModelProperty(value = "重试次数，默认值为3，最大值为10次", dataType = "Integer", required = false)
	@Range(min = 0, max = 10, message = "重试次数最大值为10次")
	private Integer retryTimes;

	/**
	 * 是否需要回调，0-否，1-是，默认为0，回调重试次数固定为3
	 */
	@ApiModelProperty(value = "是否需要回调，0-否，1-是，默认为0", dataType = "Integer", required = false)
	@Range(min = 0, max = 1, message = "是否需要回调标记取值只能是0或1")
	private Integer callbackRequired;

	/**
	 * 创建人ID，最长20位，新增时不可为空，不可修改
	 */
	@ApiModelProperty(value = "创建人ID，最长20位，新增时不可为空，不可修改", dataType = "String", required = true)
	@NotNull(message = "创建人ID不能为空", groups = { ValidationGroup.Insert.class })
	@Size(max = 20, message = "创建人ID最长20位")
	private String createdBy;

	/**
	 * 创建时间，默认值为系统当前时间
	 */
	@ApiModelProperty(value = "创建时间，默认值为系统当前时间", dataType = "LocalDateTime", required = false)
	private LocalDateTime createTime;

	/**
	 * 最后修改人ID，最长20位，修改时不可为空
	 */
	@ApiModelProperty(value = "最后修改人ID，最长20位，修改时不可为空", dataType = "String", required = true)
	@NotNull(message = "最后修改人ID不能为空", groups = { ValidationGroup.Update.class })
	@Size(max = 20, message = "最后修改人ID最长20位")
	private String updatedBy;

	/**
	 * 最后修改时间，默认值为系统当前时间，数据修改时自动更新
	 */
	@ApiModelProperty(value = "最后修改时间，默认值为系统当前时间，数据修改时自动更新", dataType = "LocalDateTime", required = false)
	private LocalDateTime updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
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

	public Integer getCallbackRequired() {
		return callbackRequired;
	}

	public void setCallbackRequired(Integer callbackRequired) {
		this.callbackRequired = callbackRequired;
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
		sbuilder.append("MessageForwardingConfigVO[").append("id=").append(id).append(", messageId=").append(messageId)
				.append(", targetSystem=").append(targetSystem).append(", forwardingWay=").append(forwardingWay)
				.append(", targetAddress=").append(targetAddress).append(", retryTimes=").append(retryTimes)
				.append(", callbackRequired=").append(callbackRequired).append(", createdBy=").append(createdBy)
				.append(", createTime=").append(createTime).append(", updatedBy=").append(updatedBy)
				.append(", updateTime=").append(updateTime).append("]");
		return sbuilder.toString();
	}

}
