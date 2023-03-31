package com.allen.message.forwarding.metadata.dao;

import com.allen.message.forwarding.metadata.model.AmfMessageForwardingConfigDO;

import java.util.List;

/**
 * 消息配置管理DAO层接口
 *
 * @author Allen
 * @date 2020年10月19日
 * @since 1.0.0
 */
public interface MessageForwardingConfigDAO {

    /**
     * 新增消息转发配置信息
     *
     * @param messageConfigDO 消息转发配置信息
     * @return 新增成功的数量
     */
    int save(AmfMessageForwardingConfigDO messageForwardingConfigDO);

    /**
     * 修改消息转发配置信息
     *
     * @param messageConfigDO 消息转发配置信息
     * @return 修改成功的数量
     */
    int update(AmfMessageForwardingConfigDO messageForwardingConfigDO);

    /**
     * 根据主键获取消息转发配置信息
     *
     * @param id 主键ID
     * @return 消息转发配置信息
     */
    AmfMessageForwardingConfigDO get(Long id);

    /**
     * 根据消息ID获取消息转发配置信息数量
     *
     * @param messageId 消息ID
     * @return 消息转发配置数量
     */
    int count(Integer messageId);

    /**
     * 根据消息ID获取消息转发配置信息
     *
     * @param messageId 消息ID
     * @return 消息转发配置信息
     */
    List<AmfMessageForwardingConfigDO> list(Integer messageId);

}
