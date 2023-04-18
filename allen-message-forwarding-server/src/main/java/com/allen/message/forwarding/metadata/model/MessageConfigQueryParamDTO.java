package com.allen.message.forwarding.metadata.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 消息配置信息查询参数
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
@ApiModel("消息配置信息查询参数")
public class MessageConfigQueryParamDTO {

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

}
