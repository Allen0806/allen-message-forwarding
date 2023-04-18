package com.allen.message.forwarding.metadata.controller;

import com.allen.message.forwarding.metadata.model.MessageConfigQueryParamDTO;
import com.allen.message.forwarding.metadata.model.MessageConfigVO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.BaseResult;
import com.allen.tool.result.PagingQueryResult;
import com.allen.tool.validation.ValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

/**
 * 消息配置管理Controller
 *
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
@Api(tags = {"消息配置管理管理接口"})
@RefreshScope
@RestController
@RequestMapping(path = "/mf/meta/mc")
public class MessageConfigController {

    /**
     * 消息配置管理服务实例
     */
    @Autowired
    @Qualifier("messageConfigServiceImpl")
    private MessageConfigService messageConfigService;

    /**
     * 新增消息配置信息
     *
     * @param messageConfigVO 消息配置信息
     * @return 新增结果
     */
    @ApiOperation("新增消息配置信息")
    @PostMapping("/save")
    public BaseResult<Object> save(@NotNull(message = "消息配置信息不能为空") @Validated({ValidationGroup.Insert.class,
            Default.class}) @RequestBody MessageConfigVO messageConfigVO) {
        messageConfigService.save(messageConfigVO);
        return BaseResult.success();
    }

    /**
     * 修改消息配置信息
     *
     * @param messageConfigVO 消息配置信息
     * @return 修改结果
     */
    @ApiOperation("修改消息配置信息")
    @PostMapping("/update")
    public BaseResult<Object> update(@NotNull(message = "消息配置信息不能为空") @Validated({ValidationGroup.Update.class,
            Default.class}) @RequestBody MessageConfigVO messageConfigVO) {
        messageConfigService.update(messageConfigVO);
        return BaseResult.success();
    }

    /**
     * 根据消息ID删除消息配置信息，逻辑删除。如果有对应的消息转发配置信息，则不允许删除
     *
     * @param messageId 消息ID
     * @param updatedBy 修改人ID
     * @return 删除结果
     */
    @ApiOperation("根据消息ID删除消息配置信息，逻辑删除。如果有对应的消息转发配置信息，则不允许删除")
    @PostMapping("/remove/{messageId}/{updatedBy}")
    public BaseResult<Object> remove(@NotNull(message = "消息ID不能为空") @PathVariable("messageId") Integer messageId,
                                     @NotNull(message = "修改人ID不能为空") @PathVariable("updatedBy") String updatedBy) {
        messageConfigService.remove(messageId, updatedBy);
        return BaseResult.success();
    }

    /**
     * 根据主键ID获取未删除的消息配置信息
     *
     * @param id 主键ID
     * @return 消息配置信息
     */
    @ApiOperation("根据主键ID获取未删除的消息配置信息")
    @PostMapping("/get/{id}")
    public BaseResult<MessageConfigVO> get(@NotNull(message = "消息配置信息主键ID不能为空") @PathVariable("id") Long id) {
        MessageConfigVO messageConfigVO = messageConfigService.get(id);
        return BaseResult.success(messageConfigVO);
    }

    /**
     * 分页查询未标记为删除的消息配置信息
     *
     * @param pagingQueryParam 分页查询条件
     * @return 分页查询结果
     */
    @ApiOperation("分页查询未标记为删除的消息配置信息")
    @PostMapping("/list_for_paging")
    public BaseResult<PagingQueryResult<MessageConfigVO>> list4Paging(@Validated @RequestBody PagingQueryParam<MessageConfigQueryParamDTO> pagingQueryParam) {
        return BaseResult.success(messageConfigService.list4Paging(pagingQueryParam));
    }
}
