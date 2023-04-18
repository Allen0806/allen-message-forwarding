package com.allen.message.forwarding.process.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

/**
 * 消息查询参数封装类
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 */
@ApiModel("消息查询参数封装对象")
public class MessageQueryParamDTO implements java.io.Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -3035184118523184186L;

    /**
     * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
     */
    @ApiModelProperty(value = "消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复")
    @Size(min = 32, max = 32, message = "消息流水号固定32位")
    private String messageNo;

    /**
     * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
     */
    @ApiModelProperty(value = "消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位")
    @Size(max = 32, message = "消息关键字最长32位")
    private String messageKeyword;

    /**
     * 消息ID，即消息配置信息里的消息ID，固定6位
     */
    @ApiModelProperty(value = "消息ID，消息配置信息里的消息ID，固定6位")
    @Range(min = 100000, max = 999999, message = "消息ID取值范围为100000~999999")
    private Integer messageId;

    /**
     * 业务线ID，最长20位
     */
    @ApiModelProperty(value = "业务线ID，最长20位")
    @Size(max = 20, message = "业务线ID最长20位")
    private String businessLineId;

    /**
     * 来源系统ID，固定4位
     */
    @ApiModelProperty(value = "来源系统ID，固定4位")
    @Range(min = 1000, max = 9999, message = "来源系统ID取值范围为1000~9999")
    private Integer sourceSystemId;

    /**
     * 通过Http接口转发消息时，设置到http header里的参数，比如接口编号等
     */
    @ApiModelProperty(value = "起始日期，格式：yyyy-MM-dd")
    private String beginDate;

    /**
     * 要转发的消息内容
     */
    @ApiModelProperty(value = "截止日期，格式：yyyy-MM-dd")
    private String endDate;

    /**
     * 分页查询时的页数，当messageNo为空时startNo和pageNo必须给定其一，如果都给定，以startNo为准
     */
    @ApiModelProperty(value = "分页查询时的页数，当messageNo为空时startNo和pageNo必须给定其一，如果都给定，以startNo为准")
    private Integer pageNo;

    /**
     * 分页查询时起始行号，从0开始。当messageNo为空时startNo和pageNo必须给定其一
     */
    @ApiModelProperty(value = "分页查询时起始行号，从0开始。当messageNo为空时startNo和pageNo必须给定其一，如果都给定，以startNo为准")
    private Integer startNo;

    /**
     * 分页查询时每页行数，最小值为1，最大值为1000。当startNo不为空时必须给定
     */
    @ApiModelProperty(value = "分页查询时每页行数，最小值为1，最大值为1000。当messageNo为空时必须给定")
    @Range(min = 1, max = 1000, message = "每页行数取值范围为1～1000")
    private Integer pageSize;

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

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getStartNo() {
        return startNo;
    }

    public void setStartNo(Integer startNo) {
        this.startNo = startNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("MessageQueryParamDTO[").append("messageNo=").append(messageNo)
                .append(", messageKeyword=").append(messageKeyword).append(", messageId=").append(messageId)
                .append(", businessLineId=").append(businessLineId).append(", sourceSystemId=").append(sourceSystemId)
                .append(", beginDate=").append(beginDate).append(", endDate=").append(endDate).append(", startNo=")
                .append(startNo).append(", pageSize=").append(pageSize).append("]").toString();
    }
}
