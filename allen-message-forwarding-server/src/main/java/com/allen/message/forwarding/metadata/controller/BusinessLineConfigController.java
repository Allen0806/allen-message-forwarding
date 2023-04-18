package com.allen.message.forwarding.metadata.controller;

import com.allen.message.forwarding.config.MessageProperty;
import com.allen.message.forwarding.metadata.model.BusinessLineConfigQueryParamDTO;
import com.allen.message.forwarding.metadata.model.BusinessLineConfigVO;
import com.allen.message.forwarding.metadata.service.BusinessLineConfigService;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.BaseResult;
import com.allen.tool.result.PagingQueryResult;
import com.allen.tool.validation.ValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息所属业务线配置信息管理Controller层
 *
 * @author Allen
 * @date 2020年10月16日
 * @since 1.0.0
 */
@Api(tags = {"消息所属业务线配置信息管理接口"})
@RefreshScope
@RestController
@RequestMapping(path = "/mf/meta/blc")
public class BusinessLineConfigController {

    /**
     * 业务线配置管理服务实例
     */
    @Autowired
    private BusinessLineConfigService businessLineConfigService;

    /**
     * 消息保留天数
     */
    @Value("${allen.message.forwarding.message.retention-days}")
    private Integer retentionDays;

    @Autowired
    private MessageProperty messageProperty;

    /**
     * 新增消息所属业务线配置信息
     *
     * @param businessLineConfigVO 消息所属业务线配置信息
     * @return 新增结果
     */
    @ApiOperation("新增消息所属业务线配置信息")
    @PostMapping("/save")
    public BaseResult<Object> save(@Validated({ValidationGroup.Insert.class, Default.class}) @RequestBody BusinessLineConfigVO businessLineConfigVO) {
        businessLineConfigService.save(businessLineConfigVO);
        return BaseResult.success();
    }

    /**
     * 修改消息所属业务线配置信息
     *
     * @param businessLineConfigVO 消息所属业务线配置信息
     * @return 修改结果
     */
    @ApiOperation("修改消息所属业务线配置信息")
    @PostMapping("/update")
    public BaseResult<Object> update(@Validated({ValidationGroup.Update.class, Default.class}) @RequestBody BusinessLineConfigVO businessLineConfigVO) {
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
    @ApiOperation("根据主键ID删除消息所属业务线配置信息，逻辑删除。如果有对应的消息来源系统配置信息，则不允许删除")
    @PostMapping("/remove/{id}/{updatedBy}")
    public BaseResult<Object> remove(@PathVariable("id") Long id, @PathVariable("updatedBy") String updatedBy) {
        businessLineConfigService.remove(id, updatedBy);
        return BaseResult.success();
    }

    /**
     * 根据主键ID获取未标记为删除的所属业务线配置信息
     *
     * @param id 主键ID
     * @return 所属业务线配置信息
     */
    @ApiOperation("根据主键ID获取未标记为删除的所属业务线配置信息")
    @PostMapping("/get/{id}")
    public BaseResult<BusinessLineConfigVO> get(@PathVariable("id") Long id) {
        BusinessLineConfigVO businessLineConfigVO = businessLineConfigService.get(id);
        return BaseResult.success(businessLineConfigVO);
    }

    /**
     * 根据业务线ID获取未标记为删除的所属业务线配置信息
     *
     * @param businessLineId 业务线ID
     * @return 所属业务线配置信息
     */
    @ApiOperation("根据业务线ID获取未标记为删除的所属业务线配置信息")
    @PostMapping("/get_by_business_line_id/{businessLineId}")
    public BaseResult<BusinessLineConfigVO> getByBusinessLineId(@PathVariable("businessLineId") String businessLineId) {
        BusinessLineConfigVO businessLineConfigVO = businessLineConfigService.getByBusinessLineId(businessLineId);
        return BaseResult.success(businessLineConfigVO);
    }

    /**
     * 根据业务线ID或（和）业务线名称模糊查询未删除的业务线信息，业务线ID和业务线名称不可同时为空
     *
     * @param queryParam 查询参数
     * @return 查询到的业务线信息
     */
    @ApiOperation("根据业务线ID或（和）业务线名称模糊查询未删除的业务线信息，业务线ID和业务线名称不可同时为空")
    @PostMapping("/list_for_fuzzy")
    public BaseResult<List<BusinessLineConfigVO>> list4Fuzzy(@Valid @RequestBody BusinessLineConfigQueryParamDTO queryParam) {
        List<BusinessLineConfigVO> businessLineConfigVOList = businessLineConfigService.list4Fuzzy(queryParam);
        return BaseResult.success(businessLineConfigVOList);
    }

    /**
     * 分页查询未标记为删除的所属业务线配置信息
     *
     * @param pagingQueryParam 分页查询参数
     * @return 分页查询结果
     */
    @ApiOperation("分页查询未标记为删除的所属业务线配置信息")
    @PostMapping("/list_for_paging")
    public BaseResult<PagingQueryResult<BusinessLineConfigVO>> list4Paging(@Validated @RequestBody PagingQueryParam<BusinessLineConfigQueryParamDTO> pagingQueryParam) {
        PagingQueryResult<BusinessLineConfigVO> pagingQueryResult = businessLineConfigService.list4Paging(pagingQueryParam);
        return BaseResult.success(pagingQueryResult);
    }

    /**
     * 获取消息保留天数
     *
     * @return 结果
     */
    @ApiOperation("获取消息保留天数")
    @PostMapping("/retention_day/get")
    public BaseResult<Map<String, Integer>> getRetentionDays() {
        Map<String, Integer> result = new HashMap<>();
        result.put("retentionDays", messageProperty.getRetentionDays());
        return BaseResult.success(result);
    }
}
