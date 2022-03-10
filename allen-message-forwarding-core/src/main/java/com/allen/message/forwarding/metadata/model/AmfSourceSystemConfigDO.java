package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息来源系统配置信息类，用来存取消息来源系统配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
public class AmfSourceSystemConfigDO implements Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 1421855185252976800L;

	/**
	 * 主键ID，修改时不可为空
	 */
	private Long id;

	/**
	 * 业务线主键ID
	 */
	private Long businessLineConfigId;

	/**
	 * 来源系统ID，固定4位，由系统分配，默认值为数据库中的最大值加1，不可修改
	 */
	private Integer sourceSystemId;

	/**
	 * 来源系统名称，最长30位，新增时不可为空
	 */
	private String sourceSystemName;

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

	public Long getBusinessLineConfigId() {
		return businessLineConfigId;
	}

	public void setBusinessLineConfigId(Long businessLineConfigId) {
		this.businessLineConfigId = businessLineConfigId;
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
		sbuilder.append("AmfSourceSystemConfigDO[").append("id=").append(id).append(", businessLineConfigId=")
				.append(businessLineConfigId).append(", sourceSystemId=").append(sourceSystemId)
				.append(", sourceSystemName=").append(sourceSystemName).append(", deleted=").append(deleted)
				.append(", createdBy=").append(createdBy).append(", createTime=").append(createTime)
				.append(", updatedBy=").append(updatedBy).append(", updateTime=").append(updateTime).append("]");
		return sbuilder.toString();
	}

}
