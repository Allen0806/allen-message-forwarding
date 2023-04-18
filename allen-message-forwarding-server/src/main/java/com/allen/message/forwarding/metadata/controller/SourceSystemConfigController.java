package com.allen.message.forwarding.metadata.controller;

import com.allen.message.forwarding.metadata.model.SourceSystemConfigQureyParamDTO;
import com.allen.message.forwarding.metadata.model.SourceSystemConfigVO;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.BaseResult;
import com.allen.tool.result.PagingQueryResult;
import com.allen.tool.validation.ValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

/**
 * 消息来源系统管理Controller层
 *
 * @author Allen
 * @date Aug 3, 2020
 * @since 1.0.0
 */
@Api(tags = {"消息来源系统管理接口"})
@RefreshScope
@RestController
@RequestMapping(path = "/mf/meta/ssc")
public class SourceSystemConfigController {

    /**
     * 日志纪录器
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceSystemConfigController.class);

    /**
     * 消息来源系统服务实例
     */
    @Autowired
    private SourceSystemConfigService sourceSystemConfigService;

    /**
     * 新增消息来源系统配置信息
     *
     * @param sourceSystemConfigVO 消息来源系统配置信息
     * @return 保存结果
     */
    @ApiOperation("新增消息来源系统配置信息")
    @PostMapping("/save")
    public BaseResult<Object> save(@NotNull(message = "消息来源系统配置信息不能为空") @Validated({ValidationGroup.Insert.class,
            Default.class}) @RequestBody SourceSystemConfigVO sourceSystemConfigVO) {
        sourceSystemConfigService.save(sourceSystemConfigVO);
        return BaseResult.success();
    }

    /**
     * 修改消息来源系统配置信息
     *
     * @param sourceSystemConfigVO
     * @return 修改结果
     */
    @ApiOperation("修改消息来源系统配置信息")
    @PostMapping("/update")
    public BaseResult<Object> update(@NotNull(message = "消息来源系统配置信息不能为空") @Validated({ValidationGroup.Update.class,
            Default.class}) @RequestBody SourceSystemConfigVO sourceSystemConfigVO) {
        sourceSystemConfigService.update(sourceSystemConfigVO);
        return BaseResult.success();
    }

    /**
     * 根据主键ID删除消息来源系统配置信息，逻辑删除。如果有对应的消息配置，则不允许删除
     *
     * @param id        主键ID
     * @param updatedBy 修改人ID
     * @return 删除结果
     */
    @ApiOperation("根据主键ID删除消息来源系统配置信息，逻辑删除。如果有对应的消息配置，则不允许删除")
    @PostMapping("/remove/{id}/{updatedBy}")
    public BaseResult<Object> remove(@PathVariable("id") Long id, @PathVariable("updatedBy") String updatedBy) {
        sourceSystemConfigService.remove(id, updatedBy);
        return BaseResult.success();
    }

    /**
     * 根据主键ID获取消息来源系统配置信息
     *
     * @param id 主键ID
     * @return 消息来源系统配置信息
     */
    @ApiOperation("根据主键ID获取消息来源系统配置信息")
    @PostMapping("/get/{id}")
    public BaseResult<SourceSystemConfigVO> get(@PathVariable("id") Long id) {
        SourceSystemConfigVO sourceSystemConfig = sourceSystemConfigService.get(id);
        return BaseResult.success(sourceSystemConfig);
    }

    /**
     * 分页查询消息来源系统配置信息
     *
     * @param pagingQueryParam 查询参数
     * @return 分页查询结果
     */
    @ApiOperation("分页查询消息来源系统配置信息")
    @PostMapping("/list_for_paging")
    public BaseResult<PagingQueryResult<SourceSystemConfigVO>> list4Paging(@Validated @RequestBody PagingQueryParam<SourceSystemConfigQureyParamDTO> pagingQueryParam) {
        return BaseResult.success(sourceSystemConfigService.list4Paging(pagingQueryParam));
    }
}
