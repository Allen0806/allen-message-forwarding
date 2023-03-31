package com.allen.message.forwarding.metadata.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息配置信息类，用来存取消息配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
public class AmfMessageConfigDO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 9173592096801438039L;

    /**
     * 主键ID，修改时不可为空
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
     * 来源系统ID，固定4位
     */
    private Integer sourceSystemId;

    /**
     * 来源系统名称，最长30位
     */
    private String sourceSystemName;

    /**
     * 消息ID，由6位数字组成，步长为1，初始值为100000，全局唯一，默认系统自动生成
     */
    private Integer messageId;

    /**
     * 消息名称，最长30位，新增时不可为空
     */
    private String messageName;

    /**
     * 消息状态：0-停用，1-启用，默认为1
     */
    private Integer messageStatus;

    /**
     * 消息发送结果回调地址，最长200位
     */
    private String callbackUrl;

    /**
     * 是否删除标记：0-否，1-是，默认为0
     */
    private Integer deleted;

    /**
     * 创建人ID，最长20位，新增时不可为空，不可修改
     */
    private String createdBy;

    /**
     * 创建时间，默认值为系统当前时间，不可修改
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
        sbuilder.append("AmfMessageConfigDO[").append("id=").append(id).append(", businessLineId=")
                .append(businessLineId).append(", businessLineName=").append(businessLineName)
                .append(", sourceSystemId=").append(sourceSystemId).append(", sourceSystemName=")
                .append(sourceSystemName).append(", messageId=").append(messageId).append(", messageName=")
                .append(messageName).append(", messageStatus=").append(messageStatus).append(", callbackUrl=")
                .append(callbackUrl).append(", deleted=").append(deleted).append(", createdBy=").append(createdBy)
                .append(", createTime=").append(createTime).append(", updatedBy=").append(updatedBy)
                .append(", updateTime=").append(updateTime).append("]");
        return sbuilder.toString();
    }

}
