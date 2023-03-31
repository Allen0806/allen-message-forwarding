package com.allen.message.forwarding.process.service;

import com.allen.message.forwarding.process.model.MessageForwardingDTO;

/**
 * 消息转发结果回调顶层接口，各回调方式需要实现该接口，策略模式
 *
 * @author Allen
 * @date 2020年12月2日
 * @since 1.0.0
 */
public interface MessageCallback {

    /**
     * 回调方法
     *
     * @param messageForwardingDTO 转发明细
     */
    void callback(MessageForwardingDTO messageForwardingDTO);
}
