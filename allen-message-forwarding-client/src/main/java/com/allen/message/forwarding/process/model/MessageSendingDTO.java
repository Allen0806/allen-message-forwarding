package com.allen.message.forwarding.process.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * 消息发送对象
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 */
@ApiModel("消息发送对象")
public class MessageSendingDTO implements java.io.Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 3413492938377456162L;

    /**
     * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
     */
    @ApiModelProperty(value = "消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复", required = true)
    @NotNull(message = "消息流水号不能为空")
    @Size(min = 32, max = 32, message = "消息流水号固定32位")
    private String messageNo;

    /**
     * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
     */
    @ApiModelProperty(value = "消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位", required = true)
    @NotNull(message = "消息关键字不能为空")
    @Size(max = 32, message = "消息关键字最长32位")
    private String messageKeyword;

    /**
     * 消息ID，即消息配置信息里的消息ID，固定6位
     */
    @ApiModelProperty(value = "消息ID，消息配置信息里的消息ID，固定6位", required = true)
    @NotNull(message = "消息ID不能为空")
    @Range(min = 100000, max = 999999, message = "消息ID取值范围为100000~999999")
    private Integer messageId;

    /**
     * 业务线ID，最长20位
     */
    @ApiModelProperty(value = "业务线ID，最长20位", required = true)
    @NotNull(message = "业务线ID不能为空")
    @Size(max = 20, message = "业务线ID最长20位")
    private String businessLineId;

    /**
     * 来源系统ID，固定4位
     */
    @ApiModelProperty(value = "来源系统ID，固定4位", required = true)
    @NotNull(message = "来源系统ID不能为空")
    @Range(min = 1000, max = 9999, message = "来源系统ID取值范围为1000~9999")
    private Integer sourceSystemId;

    /**
     * 通过Http接口转发消息时，设置到http header里的参数，比如接口编号等
     */
    @ApiModelProperty(value = "Http Header 参数，通过Http接口转发消息时，设置到http header里的参数，比如接口编号等")
    private Map<String, String> httpHeaders;

    /**
     * 要转发的消息内容
     */
    @ApiModelProperty(value = "消息内容", required = true)
    @NotNull(message = "消息内容不能为空")
    private String messageContent;

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

    public String getBusinessLineId() {
        return businessLineId;
    }

    public void setBusinessLineId(String businessLineId) {
        this.businessLineId = businessLineId;
    }

    public Integer getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(Integer sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("MessageDTO[").append("messageNo=").append(messageNo)
                .append(", messageKeyword=").append(messageKeyword).append(", messageId=").append(messageId)
                .append(", businessLineId=").append(businessLineId).append(", sourceSystemId=").append(sourceSystemId)
                .append(", httpHeaders=").append(httpHeaders).append(", messageContent=").append(messageContent)
                .append("]").toString();
    }

}
