package com.allen.message.forwarding.metadata.controller;

import com.allen.message.forwarding.metadata.model.MessageForwardingConfigVO;
import com.allen.message.forwarding.metadata.service.MessageForwardingConfigService;
import com.allen.tool.result.BaseResult;
import com.allen.tool.validation.ValidationGroup;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;
import java.util.List;

/**
 * 消息转发配置Controller
 *
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
@Api(tags = {"消息转发配置管理接口"})
@RefreshScope
@RestController
@RequestMapping(path = "/mf/meta/mfc")
public class MessageForwardingConfigController {

    /**
     * 消息转发配置管理服务实例
     */
    @Autowired
    private MessageForwardingConfigService messageForwardingConfigService;

    /**
     * 新增消息转发配置信息
     *
     * @param messageForwardingConfigVO 消息转发配置信息
     * @return 新增结果
     */
    @ApiOperation("新增消息转发配置信息")
    @PostMapping("/save")
    public BaseResult<Object> save(@Validated({ValidationGroup.Insert.class, Default.class}) @RequestBody MessageForwardingConfigVO messageForwardingConfigVO) {
        messageForwardingConfigService.save(messageForwardingConfigVO);
        return BaseResult.success();
    }

    /**
     * 修改消息转发配置信息
     *
     * @param messageForwardingConfigVO 消息转发配置信息
     * @return 修改结果
     */
    @ApiOperation("修改消息转发配置信息")
    @PostMapping("/update")
    public BaseResult<Object> update(@Validated({ValidationGroup.Update.class, Default.class}) @RequestBody MessageForwardingConfigVO messageForwardingConfigVO) {
        messageForwardingConfigService.update(messageForwardingConfigVO);
        return BaseResult.success();
    }

    /**
     * 根据主键删除消息转发配置信息，逻辑删除
     *
     * @param id        主键ID
     * @param updatedBy 修改人ID
     * @return 删除结果
     */
    @ApiOperation("根据主键删除消息转发配置信息，逻辑删除")
    @PostMapping("/remove/{id}/{updatedBy}")
    public BaseResult<Object> remove(@PathVariable("id") Long id, @PathVariable("updatedBy") String updatedBy) {
        messageForwardingConfigService.remove(id, updatedBy);
        return BaseResult.success();
    }

    /**
     * 根据主键获取消息转发配置信息
     *
     * @param id 主键ID
     * @return 消息转发配置信息
     */
    @ApiOperation("根据主键获取消息转发配置信息")
    @PostMapping("/get/{id}")
    public BaseResult<MessageForwardingConfigVO> get(@PathVariable("id") Long id) {
        MessageForwardingConfigVO messageForwardingConfigVO = messageForwardingConfigService.get(id);
        return BaseResult.success(messageForwardingConfigVO);
    }

    /**
     * 根据消息ID获取消息转发配置信息
     *
     * @param messageId 消息ID
     * @return 消息转发配置信息
     */
    @ApiOperation("根据消息ID获取消息转发配置信息")
    @PostMapping("/list/{messageId}")
    public BaseResult<List<MessageForwardingConfigVO>> list(@PathVariable("messageId") Integer messageId) {
        List<MessageForwardingConfigVO> MessageForwardingConfigVOList = messageForwardingConfigService.list(messageId);
        return BaseResult.success(MessageForwardingConfigVOList);
    }
}
