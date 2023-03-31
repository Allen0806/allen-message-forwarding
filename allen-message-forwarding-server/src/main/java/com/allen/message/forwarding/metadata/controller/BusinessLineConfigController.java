package com.allen.message.forwarding.metadata.controller;

import com.allen.message.forwarding.metadata.model.BusinessLineConfigVO;
import com.allen.message.forwarding.metadata.service.BusinessLineConfigService;
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
 * 消息所属业务线配置信息管理Controller层
 *
 * @author Allen
 * @date 2020年10月16日
 * @since 1.0.0
 */
@RestController
@RequestMapping(path = "/mf/meta/blc")
public class BusinessLineConfigController {

    /**
     * 日志纪录器
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessLineConfigController.class);

    /**
     * 业务线配置管理服务实例
     */
    @Autowired
    private BusinessLineConfigService businessLineConfigService;

    /**
     * 新增消息所属业务线配置信息
     *
     * @param businessLineConfigVO 消息所属业务线配置信息
     * @return 新增结果
     */
    @PostMapping("/save")
    public BaseResult<Object> save(@NotNull(message = "消息所属业务线配置信息不能为空") @Validated({ValidationGroup.Insert.class,
            Default.class}) @RequestBody BusinessLineConfigVO businessLineConfigVO) {
        businessLineConfigService.save(businessLineConfigVO);
        return BaseResult.success();
    }

    /**
     * 修改消息所属业务线配置信息
     *
     * @param businessLineConfigVO 消息所属业务线配置信息
     * @return 修改结果
     */
    @PostMapping("/update")
    public BaseResult<Object> update(@NotNull(message = "消息所属业务线配置信息不能为空") @Validated({ValidationGroup.Update.class,
            Default.class}) @RequestBody BusinessLineConfigVO businessLineConfigVO) {
        businessLineConfigService.update(businessLineConfigVO);
        return BaseResult.success();
    }

    /**
     * 根据主键ID删除消息所属业务线配置信息，逻辑删除。如果有对应的消息来源系统配置信息，则不允许删除
     *
     * @param id        主键ID
     * @param updatedBy 修改人ID
     * @return 删除结果
     */
    @PostMapping("/remove/{id}/{updatedBy}")
    public BaseResult<Object> remove(@NotNull(message = "主键ID不能为空") @PathVariable("id") Long id,
                                     @NotNull(message = "修改人ID不能为空") @PathVariable("updatedBy") String updatedBy) {
        businessLineConfigService.remove(id, updatedBy);
        return BaseResult.success();
    }

    /**
     * 根据主键ID获取未标记为删除的所属业务线配置信息
     *
     * @param id 主键ID
     * @return 所属业务线配置信息
     */
    @PostMapping("/get/{id}")
    public BaseResult<BusinessLineConfigVO> get(@NotNull(message = "消息所属业务线配置信息主键ID不能为空") @PathVariable("id") Long id) {
        BusinessLineConfigVO businessLineConfigVO = businessLineConfigService.get(id);
        return BaseResult.success(businessLineConfigVO);
    }

    /**
     * 根据业务线ID或（和）业务线名称模糊查询未删除的业务线信息，业务线ID和业务线名称不可同时为空，根据最左匹配规则查询
     *
     * @param businessLineId   业务线ID模糊信息，即可以为业务线ID的左侧部分信息
     * @param businessLineName 业务线名称模糊信息，即可以为业务线名称的左侧部分信息
     * @return 查询到的业务线信息
     */
    @PostMapping("/list_for_fuzzy/{businessLineId}/{businessLineName}")
    public BaseResult<List<BusinessLineConfigVO>> list4Fuzzy(@PathVariable("businessLineId") String businessLineId,
                                                             @PathVariable("businessLineName") String businessLineName) {
        List<BusinessLineConfigVO> businessLineConfigVOList = businessLineConfigService.list4Fuzzy(businessLineId,
                businessLineName);
        return BaseResult.success(businessLineConfigVOList);
    }

    /**
     * 获取未标记为删除的所属业务线配置信息数量
     *
     * @return 所属业务线配置信息数量
     */
    @PostMapping("/count")
    public BaseResult<Integer> count() {
        Integer count = businessLineConfigService.count();
        return BaseResult.success(count);
    }

    /**
     * 分页查询未标记为删除的所属业务线配置信息
     *
     * @param pageNo   当前页数
     * @param pageSize 每页行数
     * @return 分页查询结果
     */
    @PostMapping("/list_for_paging/{pageNo}/{pageSize}")
    public BaseResult<List<BusinessLineConfigVO>> list4Paging(
            @NotNull(message = "当前页数不能为空") @PathVariable("pageNo") Integer pageNo,
            @NotNull(message = "每页行数不能为空") @PathVariable("pageSize") Integer pageSize) {
        List<BusinessLineConfigVO> businessLineConfigVOList = businessLineConfigService.list4Paging(pageNo, pageSize);
        return BaseResult.success(businessLineConfigVOList);
    }
}
