package com.allen.message.forwarding.process.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

/**
 * 消息查询参数封装类
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
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
}
