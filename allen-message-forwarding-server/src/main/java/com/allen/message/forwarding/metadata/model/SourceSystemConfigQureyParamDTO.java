package com.allen.message.forwarding.metadata.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

/**
 * 消息来源系统配置信息查询参数
 *
 * @author Allen
 * @date 2020年10月17日
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
@ApiModel("消息来源系统配置信息查询参数")
public class SourceSystemConfigQureyParamDTO {

    /**
     * 业务线主键ID
     */
    @ApiModelProperty(value = "业务线主键ID")
    private Long businessLineConfigId;

    /**
     * 来源系统ID，固定4位，由系统分配，默认值为数据库中的最大值加1，不可修改
     */
    @ApiModelProperty(value = "来源系统ID，固定4位")
    @Range(min = 1000, max = 9999, message = "来源系统ID取值范围为1000~9999")
    private Integer sourceSystemId;

    /**
     * 来源系统名称，最长30位，不可为空
     */
    @ApiModelProperty(value = "来源系统名称，最长30位，不可为空")
    @Size(max = 30, message = "来源系统名称最长30位")
    private String sourceSystemName;

}
