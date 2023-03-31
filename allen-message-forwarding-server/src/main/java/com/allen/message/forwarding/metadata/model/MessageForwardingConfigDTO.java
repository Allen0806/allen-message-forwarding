package com.allen.message.forwarding.metadata.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

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
     * 主键ID，修改时不可为空
     */
    private Long id;

    /**
     * 消息ID
     */
    @ApiModelProperty(value = "消息ID", dataType = "Integer")
    private Integer messageId;

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

    /**
     * 是否需要回调，0-否，1-是
     */
    @ApiModelProperty(value = "是否需要回调，0-否，1-是，默认为0", dataType = "Integer")
    private Integer callbackRequired;

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

    @Override
    public String toString() {
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append("MessageForwardingConfigDTO[").append(", id=").append(id).append(", messageId=")
                .append(messageId).append(", targetSystem=").append(targetSystem).append(", forwardingWay=")
                .append(forwardingWay).append(", targetAddress=").append(targetAddress).append(", retryTimes=")
                .append(retryTimes).append(", callbackRequired=").append(callbackRequired).append("]");
        return sbuilder.toString();
    }
}
