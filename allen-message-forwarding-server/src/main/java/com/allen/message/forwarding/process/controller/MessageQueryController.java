package com.allen.message.forwarding.process.controller;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageForwardingQueryParamDTO;
import com.allen.message.forwarding.process.model.MessageQueryParamDTO;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.BaseResult;
import com.allen.tool.result.PagingQueryResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 消息处理Controller层
 *
 * @author Allen
 * @date 2020年5月12日
 * @since 1.0.0
 */
@Api(tags = {"消息查询接口"})
@RefreshScope
@RestController
@RequestMapping(path = "/mf/process")
public class MessageQueryController {

    /**
     * 消息管理服务
     */
    @Autowired
    private MessageManagementService messageManagementService;

    /**
     * 获取消息信息，同时返回消息转发明细
     *
     * @param messageNo 消息流水号
     * @return 消息信息
     */
    @ApiOperation("获取消息信息，同时返回消息转发明细")
    @PostMapping(value = "/get/message/{messageNo}")
    public BaseResult<MessageDTO> getMessage(@PathVariable("messageNo") String messageNo) {
        MessageDTO messageDTO = messageManagementService.getMessage(messageNo);
        if (messageDTO != null) {
            MessageForwardingQueryParamDTO forwardingQueryParam = new MessageForwardingQueryParamDTO();
            forwardingQueryParam.setMessageNo(messageNo);
            PagingQueryParam<MessageForwardingQueryParamDTO> pagingQueryParam = new PagingQueryParam<>(forwardingQueryParam, 1, 1000);
            PagingQueryResult<MessageForwardingDTO> messageForwardings = messageManagementService.listMessageForwarding4Paging(pagingQueryParam);
            messageDTO.setMessageForwardings(messageForwardings.getItems());
        }
        return BaseResult.success(messageDTO);
    }

    /**
     * 根据查询条件分页查询消息信息，不包含转发明细信息
     *
     * @param pagingQueryParam 查询条件
     * @return 消息列表
     */
    @ApiOperation("根据查询条件分页查询消息信息，不包含转发明细信息")
    @PostMapping(value = "/list/message")
    public BaseResult<PagingQueryResult<MessageDTO>> listMessage(@Valid @RequestBody PagingQueryParam<MessageQueryParamDTO> pagingQueryParam) {
        return BaseResult.success(messageManagementService.listMessage4Paging(pagingQueryParam));
    }

    /**
     * 获取消息转发明细
     *
     * @param messageNo    消息流水号
     * @param forwardingId 转发配置主键
     * @return 息转发明细信息
     */
    @ApiOperation("获取消息转发明细")
    @PostMapping(value = "/get/forwarding/{messageNo}/{forwardingId}")
    public BaseResult<MessageForwardingDTO> getMessageForwarding(@PathVariable("messageNo") String messageNo, @PathVariable("forwardingId") Long forwardingId) {
        MessageForwardingDTO forwarding = messageManagementService.getMessageForwarding(messageNo, forwardingId);
        return BaseResult.success(forwarding);
    }

    /**
     * 根据查询条件分页查询消息转发信息
     *
     * @param pagingQueryParam 查询条件
     * @return 消息转发信息列表
     */
    @ApiOperation("根据查询条件分页查询消息转发信息")
    @PostMapping(value = "/list/forwarding")
    public BaseResult<PagingQueryResult<MessageForwardingDTO>> listMessageForwarding(@Valid @RequestBody PagingQueryParam<MessageForwardingQueryParamDTO> pagingQueryParam) {
        PagingQueryResult<MessageForwardingDTO> forwardings = messageManagementService.listMessageForwarding4Paging(pagingQueryParam);
        return BaseResult.success(forwardings);
    }
}
