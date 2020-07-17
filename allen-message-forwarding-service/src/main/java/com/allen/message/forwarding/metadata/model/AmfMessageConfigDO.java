package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import com.allen.tool.validation.ValidationGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消息配置信息类，用来存取消息配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@ApiModel("消息配置信息")
public class AmfMessageConfigDO implements Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 9173592096801438039L;

	/**
	 * 主键ID，修改时不可为空
	 */
	@ApiModelProperty(value = "主键ID，修改时不可为空", dataType = "Long", required = true)
	@NotNull(message = "主键ID不能为空", groups = { ValidationGroup.Update.class })
	private Long id;

	/**
	 * 业来源系统配置主键，新增时不可为空
	 */
	@ApiModelProperty(value = "业来源系统配置主键，新增时不可为空", dataType = "Long", required = true)
	@NotNull(message = "业来源系统配置主键不能为空", groups = { ValidationGroup.Insert.class })
	private Long sourceSystemConfigId;

	/**
	 * 业务线ID，最长20位
	 */
	@ApiModelProperty(value = "业务线ID，最长20位", dataType = "String", required = false)
	@Size(max = 20, message = "业务线ID最长20位")
	private String businessLineId;

	/**
	 * 业务线名称，最长30位
	 */
	@ApiModelProperty(value = "业务线名称，最长30位", dataType = "String", required = false)
	@Size(max = 30, message = "业务线名称最长30位")
	private String businessLineName;

	/**
	 * 来源系统ID，固定4位
	 */
	@ApiModelProperty(value = "来源系统ID，固定4位", dataType = "Integer", required = false)
	@Range(min = 1000, max = 9999, message = "来源系统ID取值范围为1000~9999")
	private Integer sourceSystemId;

	/**
	 * 来源系统名称，最长30位
	 */
	@ApiModelProperty(value = "来源系统名称，最长30位", dataType = "String", required = false)
	@Size(max = 30, message = "来源系统名称最长30位")
	private String sourceSystemName;

	/**
	 * 消息ID，由6位数字组成，步长为1，初始值为100000，全局唯一，默认系统自动生成
	 */
	@ApiModelProperty(value = "消息ID，由6位数字组成，步长为1，初始值为100000，全局唯一，默认系统自动生成", dataType = "Integer", required = false)
	@Range(min = 100000, max = 999999, message = "消息ID取值范围为100000~999999")
	private Integer messageId;

	/**
	 * 消息名称，最长30位，新增时不可为空
	 */
	@ApiModelProperty(value = "消息名称，最长30位，新增时不可为空", dataType = "String", required = true)
	@NotNull(message = "消息名称不能为空", groups = { ValidationGroup.Insert.class })
	@Size(max = 30, message = "消息名称最长30位")
	private String messageName;

	/**
	 * 消息状态：0-停用，1-启用，默认为1
	 */
	@ApiModelProperty(value = "消息状态：0-停用，1-启用，默认为1", dataType = "Integer", required = false)
	@Range(min = 0, max = 1, message = "消息状态取值只能是0或1")
	private Integer messageStatus;

	/**
	 * 消息发送结果回调地址，最长200位
	 */
	@ApiModelProperty(value = "消息发送结果回调地址，最长200位", dataType = "String", required = false)
	@Size(max = 200, message = "消息发送结果回调地址最长200位")
	private String callbackUrl;

	/**
	 * 是否删除标记：0-否，1-是，默认为0
	 */
	@ApiModelProperty(value = "是否删除标记：0-否，1-是，默认为0", dataType = "Integer", required = false)
	@Range(min = 0, max = 1, message = "是否删除标记取值只能是0或1")
	private Integer deleted;

	/**
	 * 创建人ID，最长20位，新增时不可为空，不可修改
	 */
	@ApiModelProperty(value = "创建人ID，最长20位，新增时不可为空，不可修改", dataType = "String", required = true)
	@NotNull(message = "创建人ID不能为空", groups = { ValidationGroup.Insert.class })
	@Size(max = 20, message = "创建人ID最长20位")
	private String createdBy;

	/**
	 * 创建时间，默认值为系统当前时间，不可修改
	 */
	@ApiModelProperty(value = "创建时间，默认值为系统当前时间，不可修改", dataType = "LocalDateTime", required = false)
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

	public Long getSourceSystemConfigId() {
		return sourceSystemConfigId;
	}

	public void setSourceSystemConfigId(Long sourceSystemConfigId) {
		this.sourceSystemConfigId = sourceSystemConfigId;
	}

	public String getBusinessLineId() {
		return businessLineId;
	}

	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}

	public String getBusinessLineName() {
		return businessLineName;
	}

	public void setBusinessLineName(String businessLineName) {
		this.businessLineName = businessLineName;
	}

	public Integer getSourceSystemId() {
		return sourceSystemId;
	}

	public void setSourceSystemId(Integer sourceSystemId) {
		this.sourceSystemId = sourceSystemId;
	}

	public String getSourceSystemName() {
		return sourceSystemName;
	}

	public void setSourceSystemName(String sourceSystemName) {
		this.sourceSystemName = sourceSystemName;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public Integer getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(Integer messageStatus) {
		this.messageStatus = messageStatus;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
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
		sbuilder.append("AmfMessageConfigDO[").append("id=").append(id).append(", sourceSystemConfigId=")
				.append(sourceSystemConfigId).append(", businessLineId=").append(businessLineId)
				.append(", businessLineName=").append(businessLineName).append(", sourceSystemId=")
				.append(sourceSystemId).append(", sourceSystemName=").append(sourceSystemName).append(", messageId=")
				.append(messageId).append(", messageName=").append(messageName).append(", messageStatus=")
				.append(messageStatus).append(", callbackUrl=").append(callbackUrl).append(", deleted=").append(deleted)
				.append(", createdBy=").append(createdBy).append(", createTime=").append(createTime)
				.append(", updatedBy=").append(updatedBy).append(", updateTime=").append(updateTime).append("]");
		return sbuilder.toString();
	}

}
