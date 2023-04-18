package com.allen.message.forwarding.metadata.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 消息所属业务线配置信息查询参数
 *
 * @author allen
 * @date 2023年04月06日
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
@ApiModel("消息所属业务线配置信息查询参数")
public class BusinessLineConfigQueryParamDTO {
    /**
     * 业务线ID，最长20位
     */
    @ApiModelProperty(value = "业务线ID，最长20位")
    @NotBlank(message = "业务线ID不能为空")
    private String businessLineId;

    /**
     * 业务线名称，最长30位
     */
    @ApiModelProperty(value = "业务线名称，最长30位")
    private String businessLineName;
}
