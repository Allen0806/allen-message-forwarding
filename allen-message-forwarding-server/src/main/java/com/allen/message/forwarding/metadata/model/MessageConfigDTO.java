package com.allen.message.forwarding.metadata.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 消息配置信息数据传输类，包含消息转发信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@ApiModel("消息配置信息数据传输类")
public class MessageConfigDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -3708751423749019301L;

    /**
     * 消息ID，由6位数字组成，步长为1，初始值为100000，全局唯一，默认系统自动生成
     */
    @ApiModelProperty(value = "消息ID，由6位数字组成，步长为1，初始值为100000，全局唯一，默认系统自动生成")
    private Integer messageId;

    /**
     * 消息名称，最长30位
     */
    @ApiModelProperty(value = "消息名称，最长30位")
    private String messageName;

    /**
     * 业务线ID，最长20位
     */
    @ApiModelProperty(value = "业务线ID，最长20位")
    private String businessLineId;

    /**
     * 业务线名称，最长30位
     */
    @ApiModelProperty(value = "业务线名称，最长30位")
    private String businessLineName;

    /**
     * 来源系统ID，固定4位
     */
    @ApiModelProperty(value = "来源系统ID，固定4位")
    private Integer sourceSystemId;

    /**
     * 来源系统名称，最长30位
     */
    @ApiModelProperty(value = "来源系统名称，最长30位")
    private String sourceSystemName;

    /**
     * 消息发送结果回调地址，最长200位
     */
    @ApiModelProperty(value = "消息发送结果回调地址，最长200位")
    private String callbackUrl;

    /**
     * 消息转发配置信息列表
     */
    @ApiModelProperty(value = "消息转发配置信息列表")
    private List<MessageForwardingConfigDTO> forwardingConfigs;

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

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public List<MessageForwardingConfigDTO> getForwardingConfigs() {
        return forwardingConfigs;
    }

    public void setForwardingConfigs(List<MessageForwardingConfigDTO> forwardingConfigs) {
        this.forwardingConfigs = forwardingConfigs;
    }

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append("MessageConfigDTO[").append(", messageId=").append(messageId).append(", messageName=")
                .append(messageName).append(", businessLineId=").append(businessLineId).append(", businessLineName=")
                .append(businessLineName).append(", sourceSystemId=").append(sourceSystemId)
                .append(", sourceSystemName=").append(sourceSystemName).append(", callbackUrl=").append(callbackUrl)
                .append(", forwardingConfigs=").append(forwardingConfigs).append("]");
        return sbuilder.toString();
    }
}
