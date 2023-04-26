package com.allen.message.forwarding.process.controller;

import com.allen.message.forwarding.process.model.MessageSendingDTO;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.result.BaseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 消息处理Controller层
 *
 * @author Allen
 * @date 2020年5月12日
 * @since 1.0.0
 */
@Api(tags = {"消息接收接口"})
@RefreshScope
@RestController
@RequestMapping(path = "/mf/process")
public class MessageSendController {

    /**
     * 消息处理服务
     */
    @Autowired
    private MessageProcessService messageProcessService;

    /**
     * 接收消息
     *
     * @param message 消息对象
     * @return 响应对象
     */
    @ApiOperation("接收消息")
    @PostMapping(value = "/send")
    public BaseResult<Object> send(@Valid @RequestBody MessageSendingDTO message) {
        messageProcessService.send(message);
        return BaseResult.success();
    }
}
