package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;

/**
 * 消息所属业务线配置信息DO类，用来存取消息所属业务线配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@ApiModel("消息所属业务配置信息")
public class AmfBusinessLineConfigDO implements Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -1805311170469738980L;

	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 业务线ID，最长20位
	 */
	private String businessLineId;

	/**
	 * 业务线名称，最长30位
	 */
	private String businessLineName;

	/**
	 * 是否删除标记：0-否，1-是，默认为0
	 */
	private Integer deleted;

	/**
	 * 创建人ID，最长20位
	 */
	private String createdBy;

	/**
	 * 创建时间，默认值为系统当前时间
	 */
	private LocalDateTime createTime;

	/**
	 * 最后修改人ID，最长20位
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
		sbuilder.append("AmfBusinessLineConfigDO[").append("id=").append(id).append(", businessLineId=")
				.append(businessLineId).append(", businessLineName=").append(businessLineName).append(", deleted=")
				.append(deleted).append(", createdBy=").append(createdBy).append(", createTime=").append(createTime)
				.append(", updatedBy=").append(updatedBy).append(", updateTime=").append(updateTime).append("]");
		return sbuilder.toString();
	}

}
