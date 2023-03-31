package com.allen.message.forwarding.process.service;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.model.MessageForwardingQueryParamDTO;
import com.allen.message.forwarding.process.model.MessageQueryParamDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息管理服务，封装对数据库层的操作
 *
 * @author Allen
 * @date 2020年11月6日
 * @since 1.0.0
 */
public interface MessageManagementService {

    /**
     * 保存消息信息，包含消息明细信息
     *
     * @param messageDTO 消息传入对象
     */
    void save(MessageDTO messageDTO);

    /**
     * 更新消息转发明细，更新时只给定主键、上次更新时间及需要更新的属性即可，不更新的属性不要给定。 如果转发成功，则同步更新消息的转发成功次数
     *
     * @param messageForwardingDTO 消息转发明细信息
     */
    void updateForwardingResult(MessageForwardingDTO messageForwardingDTO);

    /**
     * 更新转发明细回调结果
     *
     * @param messageForwardingDTO 消息转发明细信息
     */
    void updateCallbackResult(MessageForwardingDTO messageForwardingDTO);

    /**
     * 迁移消息信息，仅限插入到历史表
     *
     * @param deadline 截止时间，即只迁移创建时间小于给定时间的数据
     */
    void migrate(LocalDateTime deadline);

    /**
     * 获取消息信息
     *
     * @param messageNo 消息流水号
     * @return 消息信息
     */
    MessageDTO getMessage(String messageNo);

    /**
     * 获取消息转发明细
     *
     * @param messageNo    消息流水号
     * @param forwardingId 转发配置主键
     * @return 息转发明细信息
     */
    MessageForwardingDTO getMessageForwarding(String messageNo, Long forwardingId);

    /**
     * 查询符合条件的消息数量
     *
     * @param messageQueryParam 查询条件
     * @return 消息数量
     */
    Integer countMessage(MessageQueryParamDTO messageQueryParam);

    /**
     * 根据查询条件查询消息信息
     *
     * @param messageQueryParam 查询条件
     * @return 消息列表
     */
    List<MessageDTO> listMessage(MessageQueryParamDTO messageQueryParam);

    /**
     * 查询符合条件的消息转发明细数量
     *
     * @param forwardingQueryParam 查询条件
     * @return 数量
     */
    Integer countMessageForwarding(MessageForwardingQueryParamDTO forwardingQueryParam);

    /**
     * 根据查询条件查询消息转发信息
     *
     * @param forwardingQueryParam 查询条件
     * @return 消息转发信息列表
     */
    List<MessageForwardingDTO> listMessageForwarding(MessageForwardingQueryParamDTO forwardingQueryParam);
}
