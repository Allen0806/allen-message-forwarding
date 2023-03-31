package com.allen.message.forwarding.metadata.model;

import com.allen.tool.validation.ValidationGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息来源系统配置信息DTO类，用来传输消息来源系统配置信息
 *
 * @author Allen
 * @date 2020年10月17日
 * @since 1.0.0
 */
@ApiModel("消息来源系统配置信息")
public class SourceSystemConfigVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -3141863322260139688L;

    /**
     * 主键ID，修改时不可为空
     */
    @ApiModelProperty(value = "主键ID，修改时不可为空", dataType = "Long", required = true)
    @NotNull(message = "主键ID不能为空", groups = {ValidationGroup.Update.class})
    private Long id;

    /**
     * 业务线主键ID，新增时不可为空
     */
    @ApiModelProperty(value = "业务线主键ID，新增时不可为空", dataType = "Long", required = true)
    @NotNull(message = "业务线主键ID不能为空", groups = {ValidationGroup.Insert.class})
    private Long businessLineConfigId;

    /**
     * 业务线ID
     */
    @ApiModelProperty(value = "业务线ID", dataType = "String", required = false)
    private String businessLineId;

    /**
     * 业务线名称
     */
    @ApiModelProperty(value = "业务线名称", dataType = "String", required = false)
    private String businessLineName;

    /**
     * 来源系统ID，固定4位，由系统分配，默认值为数据库中的最大值加1，不可修改
     */
    @ApiModelProperty(value = "来源系统ID，固定4位", dataType = "Integer", required = false)
    @Range(min = 1000, max = 9999, message = "来源系统ID取值范围为1000~9999")
    private Integer sourceSystemId;

    /**
     * 来源系统名称，最长30位，不可为空
     */
    @ApiModelProperty(value = "来源系统名称，最长30位，不可为空", dataType = "String", required = true)
    @NotNull(message = "来源系统名称不能为空")
    @Size(max = 30, message = "来源系统名称最长30位")
    private String sourceSystemName;

    /**
     * 创建人ID，最长20位，新增时不可为空，不可修改
     */
    @ApiModelProperty(value = "创建人ID，最长20位，新增时不可为空，不可修改", dataType = "String", required = true)
    @NotNull(message = "创建人ID不能为空", groups = {ValidationGroup.Insert.class})
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
    @NotNull(message = "最后修改人ID不能为空", groups = {ValidationGroup.Update.class})
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

    public Long getBusinessLineConfigId() {
        return businessLineConfigId;
    }

    public void setBusinessLineConfigId(Long businessLineConfigId) {
        this.businessLineConfigId = businessLineConfigId;
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
        sbuilder.append("SourceSystemConfigVO[").append("id=").append(id).append(", businessLineConfigId=")
                .append(businessLineConfigId).append(", businessLineId=").append(businessLineId)
                .append(", businessLineName=").append(businessLineName).append(", sourceSystemId=")
                .append(sourceSystemId).append(", sourceSystemName=").append(sourceSystemName).append(", createdBy=")
                .append(createdBy).append(", createTime=").append(createTime).append(", updatedBy=").append(updatedBy)
                .append(", updateTime=").append(updateTime).append("]");
        return sbuilder.toString();
    }

}
