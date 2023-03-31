package com.allen.message.forwarding.metadata.controller;

import com.allen.message.forwarding.metadata.model.SourceSystemConfigVO;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.tool.result.BaseResult;
import com.allen.tool.validation.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息来源系统管理Controller层
 *
 * @author Allen
 * @date Aug 3, 2020
 * @since 1.0.0
 */
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
     * @param sourceSystemConfigDO 消息来源系统配置信息
     * @return 保存结果
     */
    @PostMapping("/save")
    public BaseResult<Object> save(@NotNull(message = "消息来源系统配置信息不能为空") @Validated({ValidationGroup.Insert.class,
            Default.class}) @RequestBody SourceSystemConfigVO sourceSystemConfigVO) {
        sourceSystemConfigService.save(sourceSystemConfigVO);
        return BaseResult.success();
    }

    /**
     * 修改消息来源系统配置信息
     *
     * @param sourceSystemConfigDO
     * @return 修改结果
     */
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
    @PostMapping("/remove/{id}/{updatedBy}")
    public BaseResult<Object> remove(@NotNull(message = "主键ID不能为空") @PathVariable("id") Long id,
                                     @NotNull(message = "修改人ID不能为空") @PathVariable("updatedBy") String updatedBy) {
        sourceSystemConfigService.remove(id, updatedBy);
        return BaseResult.success();
    }

    /**
     * 根据主键ID获取消息来源系统配置信息
     *
     * @param id 主键ID
     * @return 消息来源系统配置信息
     */
    @PostMapping("/get/{id}")
    public BaseResult<SourceSystemConfigVO> get(@NotNull(message = "主键ID不能为空") @PathVariable("id") Long id) {
        SourceSystemConfigVO sourceSystemConfig = sourceSystemConfigService.get(id);
        return BaseResult.success(sourceSystemConfig);
    }

    /**
     * 根据业务线ID统计消息来源系统配置信息数量
     *
     * @param businessLineId 业务线ID
     * @return 消息来源系统配置信息数量
     */
    @PostMapping("/count/{businessLineConfigId}")
    public BaseResult<Integer> count(
            @NotNull(message = "业务线主键不能为空") @PathVariable("businessLineConfigId") Long businessLineConfigId) {
        Integer count = sourceSystemConfigService.count(businessLineConfigId);
        return BaseResult.success(count);
    }

    /**
     * 根据业务线ID分页查询消息来源系统配置信息
     *
     * @param businessLineId 业务线ID
     * @param pageNo         当前页数
     * @param pageSize       每页行数
     * @return 分页查询结果
     */
    @PostMapping("/list_for_paging/{businessLineConfigId}/{pageNo}/{pageSize}")
    public BaseResult<List<SourceSystemConfigVO>> listByBusinessLineId4Paging(
            @NotNull(message = "业务线主键不能为空") @PathVariable("businessLineConfigId") Long businessLineConfigId,
            @NotNull(message = "当前页数不能为空") @PathVariable("pageNo") Integer pageNo,
            @NotNull(message = "每页行数不能为空") @PathVariable("pageSize") Integer pageSize) {
        List<SourceSystemConfigVO> sourceSystemConfigs = sourceSystemConfigService.list4Paging(businessLineConfigId,
                pageNo, pageSize);
        return BaseResult.success(sourceSystemConfigs);
    }
}
