package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.allen.tool.validation.ValidationGroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消息所属业务线配置信息DTO类，用来存取消息所属业务线配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@ApiModel("消息所属业务配置信息")
public class AmfBusinessLineConfigDTO implements Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -3505063851603049868L;

	/**
	 * 主键ID，修改时不可为空
	 */
	@ApiModelProperty(value = "主键ID，修改时不可为空", dataType = "Long", required = true)
	@NotNull(message = "主键ID不能为空", groups = { ValidationGroup.Update.class })
	private Long id;

	/**
	 * 业务线ID，最长20位，新增时不可为空，不可修改
	 */
	@ApiModelProperty(value = "业务线ID，最长20位，新增时不可为空", dataType = "String", required = true)
	@NotNull(message = "业务线ID不能为空", groups = { ValidationGroup.Insert.class })
	@Size(max = 20, message = "业务线ID最长20位")
	private String businessLineId;

	/**
	 * 业务线名称，最长30位，新增及修改时不可为空
	 */
	@ApiModelProperty(value = "业务线名称，最长30位，新增及修改时不可为空", dataType = "String", required = true)
	@NotNull(message = "业务线名称不能为空", groups = { ValidationGroup.Insert.class, ValidationGroup.Update.class })
	@Size(max = 30, message = "业务线名称最长30位")
	private String businessLineName;

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
		sbuilder.append("AmfBusinessLineConfigDO[").append("id=").append(id).append(", businessLineId=")
				.append(businessLineId).append(", businessLineName=").append(businessLineName).append(", createdBy=")
				.append(createdBy).append(", createTime=").append(createTime).append(", updatedBy=").append(updatedBy)
				.append(", updateTime=").append(updateTime).append("]");
		return sbuilder.toString();
	}

}
