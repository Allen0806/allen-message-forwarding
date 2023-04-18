package com.allen.message.forwarding.process.model;

import java.io.Serializable;

/**
 * 用于回调的消息转发明细信息
 *
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
public class ForwardingMessage4CallbackDTO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -1612792847628823314L;

    /**
     * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
     */
    private String messageNo;

    /**
     * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
     */
    private String messageKeyword;

    /**
     * 消息ID，即消息配置信息里的消息ID，固定6位
     */
    private Integer messageId;

    /**
     * 消息转发配置主键
     */
    private Long forwardingId;

    /**
     * 转发结果：0-失败，1-成功
     */
    private Integer forwardingResult;

    public String getMessageNo() {
        return messageNo;
    }

    public void setMessageNo(String messageNo) {
        this.messageNo = messageNo;
    }

    public String getMessageKeyword() {
        return messageKeyword;
    }

    public void setMessageKeyword(String messageKeyword) {
        this.messageKeyword = messageKeyword;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Long getForwardingId() {
        return forwardingId;
    }

    public void setForwardingId(Long forwardingId) {
        this.forwardingId = forwardingId;
    }

    public Integer getForwardingResult() {
        return forwardingResult;
    }

    public void setForwardingResult(Integer forwardingResult) {
        this.forwardingResult = forwardingResult;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("ForwardingMessage4Callback[").append("messageNo=").append(messageNo)
                .append("messageKeyword=").append(messageKeyword).append(", messageId=").append(messageId)
                .append(", forwardingId=").append(forwardingId).append(", forwardingResult=").append(forwardingResult)
                .append("]").toString();
    }
}
