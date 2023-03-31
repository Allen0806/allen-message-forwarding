package com.allen.message.forwarding.process.service;

import com.allen.message.forwarding.process.model.ForwardingMessage4MQ;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageSendingDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * 消息转发处理服务层接口
 *
 * @author Allen
 * @date 2020年11月5日
 * @since 1.0.0
 */
@Validated
public interface MessageProcessService {

    /**
     * 接收上游系统发送过来的消息，保存到数据库，并发送的消息队列进行下一步转发处理
     *
     * @param messageSendingDTO 消息传入对象
     */
    void send(@NotNull(message = "消息传入对象不能为空") @Valid MessageSendingDTO messageSendingDTO);

    /**
     * 转发消息
     *
     * @param messageForwarding 消息转发明细
     */
    void forward(@NotNull(message = "消息转发明细不能为空") @Valid ForwardingMessage4MQ messageForwarding);

    /**
     * 更新转发结果，各种转发方式完成转发后需要调用此方法更新转发结果
     *
     * @param messageForwardingDTO 转发明细
     * @param forwardingResult     转发结果
     * @param needRetry            是否需要重试
     */
    void updateForwardingResult(@NotNull(message = "消息转发明细不能为空") MessageForwardingDTO messageForwardingDTO,
                                @NotNull(message = "消息转发结果不能为空") Boolean forwardingResult,
                                @NotNull(message = "是否需要重试不能为空") Boolean needRetry);

    /**
     * 转发结果回调
     *
     * @param messageForwarding 消息转发明细
     */
    void callback(@NotNull(message = "消息转发明细不能为空") @Valid ForwardingMessage4MQ messageForwarding);

    /**
     * 更新回调结果，各种回调方式完成回调后需要调用此方法更新回调结果
     *
     * @param messageForwardingDTO 转发明细
     * @param callbackResult       回调结果
     * @param needRetry            是否需要重试
     */
    void updateCallbackResult(@NotNull(message = "消息转发明细不能为空") MessageForwardingDTO messageForwardingDTO,
                              @NotNull(message = "消息回调结果不能为空") Boolean callbackResult,
                              @NotNull(message = "是否需要重试不能为空") Boolean needRetry);

    /**
     * 转发重试
     */
    void retryForward();

    /**
     * 回调重试
     */
    void retryCallback();

    /**
     * 历史消息迁移
     */
    void migrate();
}
