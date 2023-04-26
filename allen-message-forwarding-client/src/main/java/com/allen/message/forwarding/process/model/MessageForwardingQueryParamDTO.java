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
public class MessageForwardingQueryParamDTO implements java.io.Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 3309288046287419023L;

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
     * 消息转发配置主键
     */
    @ApiModelProperty(value = "消息转发配置主键")
    private Long forwardingId;

    /**
     * 转发处理状态：0-处理中，1-重试中，2-已完成
     */
    @ApiModelProperty(value = "转发处理状态")
    private Integer forwardingStatus;

    /**
     * 转发结果：0-失败，1-成功
     */
    @ApiModelProperty(value = "转发结果")
    private Integer forwardingResult;

    /**
     * 回调处理状态：0-处理中，1-重试中，2-已完成
     */
    @ApiModelProperty(value = "回调处理状态")
    private Integer callbackStatus;

    /**
     * 回调结果：0-失败，1-成功
     */
    @ApiModelProperty(value = "回调结果")
    private Integer callbackResult;

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
