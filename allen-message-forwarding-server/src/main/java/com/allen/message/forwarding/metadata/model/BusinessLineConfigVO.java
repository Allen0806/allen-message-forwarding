package com.allen.message.forwarding.metadata.model;

import com.allen.tool.validation.ValidationGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息所属业务线配置信息DTO类，用来存取消息所属业务线配置信息
 *
 * @author Allen
 * @date Jul 14, 2020
 * @since 1.0.0
 */
@Data
@ToString(callSuper = true)
@ApiModel("消息所属业务配置信息")
public class BusinessLineConfigVO implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = -3505063851603049868L;

    /**
     * 主键ID，修改时不可为空
     */
    @ApiModelProperty(value = "主键ID，修改时不可为空", required = true)
    @NotNull(message = "主键ID不能为空", groups = {ValidationGroup.Update.class})
    private Long id;

    /**
     * 业务线ID，最长20位，新增时不可为空，不可修改
     */
    @ApiModelProperty(value = "业务线ID，最长20位，新增时不可为空", required = true)
    @NotBlank(message = "业务线ID不能为空", groups = {ValidationGroup.Insert.class})
    @Size(max = 20, message = "业务线ID过长，最长20位")
    private String businessLineId;

    /**
     * 业务线名称，最长30位，新增及修改时不可为空
     */
    @ApiModelProperty(value = "业务线名称，最长30位，新增及修改时不可为空", required = true)
    @NotBlank(message = "业务线名称不能为空", groups = {ValidationGroup.Insert.class, ValidationGroup.Update.class})
    @Size(max = 30, message = "业务线名称过长，最长30位")
    private String businessLineName;

    /**
     * 创建人ID，最长20位，新增时不可为空，不可修改
     */
    @ApiModelProperty(value = "创建人ID，最长20位，新增时不可为空，不可修改", required = true)
    @NotBlank(message = "创建人ID不能为空", groups = {ValidationGroup.Insert.class})
    @Size(max = 20, message = "创建人ID过长，最长20位")
    private String createdBy;

    /**
     * 创建时间，默认值为系统当前时间
     */
    @ApiModelProperty(value = "创建时间，默认值为系统当前时间，格式：yyyy-MM-dd HH:mm:ss", required = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 最后修改人ID，最长20位，修改时不可为空
     */
    @ApiModelProperty(value = "最后修改人ID，最长20位，修改时不可为空", required = true)
    @NotBlank(message = "最后修改人ID不能为空", groups = {ValidationGroup.Update.class})
    @Size(max = 20, message = "最后修改人ID过长，最长20位")
    private String updatedBy;

    /**
     * 最后修改时间，默认值为系统当前时间，数据修改时自动更新
     */
    @ApiModelProperty(value = "最后修改时间，默认值为系统当前时间，数据修改时自动更新，格式：yyyy-MM-dd HH:mm:ss", required = false)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

}
