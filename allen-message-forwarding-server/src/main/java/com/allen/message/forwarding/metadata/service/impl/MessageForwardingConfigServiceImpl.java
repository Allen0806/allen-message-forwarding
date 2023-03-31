package com.allen.message.forwarding.metadata.service.impl;


import com.allen.message.forwarding.constant.MessageConstant;
import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.metadata.dao.MessageForwardingConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfMessageForwardingConfigDO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigVO;
import com.allen.message.forwarding.metadata.service.MessageForwardingConfigService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.string.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息转发配置服务层接口实例
 *
 * @author Allen
 * @date 2020年10月26日
 * @since 1.0.0
 */
@Service
public class MessageForwardingConfigServiceImpl implements MessageForwardingConfigService {

    /**
     * 日志纪录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardingConfigServiceImpl.class);

    /**
     * redisTemplate实例
     */
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 消息转发配置DAO层实例
     */
    @Autowired
    private MessageForwardingConfigDAO forwardingConfigDAO;

    @Transactional
    @Override
    public void save(MessageForwardingConfigVO messageForwardingConfigVO) {
        AmfMessageForwardingConfigDO messageForwardingConfigDO = toDO(messageForwardingConfigVO);
        if (StringUtil.isBlank(messageForwardingConfigDO.getUpdatedBy())) {
            messageForwardingConfigDO.setUpdatedBy(messageForwardingConfigDO.getCreatedBy());
        }
        int count = forwardingConfigDAO.save(messageForwardingConfigDO);
        if (count == 0) {
            LOGGER.error("保存消息转发配置信息失败，消息转发配置信息：{}", messageForwardingConfigDO);
            throw new CustomBusinessException(ResultStatuses.MF_0401);
        }
        LOGGER.info("保存消息转发配置信息失败，消息转发配置信息：{}", messageForwardingConfigDO);

    }

    @CacheEvict(cacheNames = MessageConstant.MESSAGE_CONFIG_CACHE_NAME, key = "#messageForwardingConfigVO.messageId")
    @Transactional
    @Override
    public void update(MessageForwardingConfigVO messageForwardingConfigVO) {
        AmfMessageForwardingConfigDO messageForwardingConfigDO = forwardingConfigDAO
                .get(messageForwardingConfigVO.getId());
        if (messageForwardingConfigDO == null) {
            LOGGER.error("不存在对应的转发配置信息，消息转发配置信息：{}", messageForwardingConfigVO);
            throw new CustomBusinessException(ResultStatuses.MF_0402);
        }
        messageForwardingConfigDO.setTargetSystem(messageForwardingConfigVO.getTargetSystem());
        messageForwardingConfigDO.setForwardingWay(messageForwardingConfigVO.getForwardingWay());
        messageForwardingConfigDO.setTargetAddress(messageForwardingConfigVO.getTargetAddress());
        messageForwardingConfigDO.setRetryTimes(messageForwardingConfigVO.getRetryTimes());
        messageForwardingConfigDO.setCallbackRequired(messageForwardingConfigVO.getCallbackRequired());
        messageForwardingConfigDO.setUpdatedBy(messageForwardingConfigVO.getUpdatedBy());
        int count = forwardingConfigDAO.update(messageForwardingConfigDO);
        if (count == 0) {
            LOGGER.error("更新消息转发配置信息失败，消息转发配置信息：{}", messageForwardingConfigDO);
            throw new CustomBusinessException(ResultStatuses.MF_0403);
        }
        LOGGER.info("更新消息转发配置信息成功，消息转发配置信息：{}", messageForwardingConfigDO);
    }

    @Transactional
    @Override
    public void remove(Long id, String updatedBy) {
        AmfMessageForwardingConfigDO messageForwardingConfigDO = forwardingConfigDAO.get(id);
        if (messageForwardingConfigDO == null) {
            LOGGER.error("不存在对应的转发配置信息，消息转发配置主键：{}", id);
            throw new CustomBusinessException(ResultStatuses.MF_0402);
        }
        messageForwardingConfigDO.setDeleted(1);
        messageForwardingConfigDO.setUpdatedBy(updatedBy);
        int count = forwardingConfigDAO.update(messageForwardingConfigDO);
        if (count == 0) {
            LOGGER.error("删除消息转发配置信息失败，消息转发配置主键：{}，删除人：{}", id, updatedBy);
            throw new CustomBusinessException(ResultStatuses.MF_0404);
        }
        // 清除缓存
        evictCache(messageForwardingConfigDO.getMessageId());
        LOGGER.info("删除消息转发配置信息成功，消息转发配置主键：{}，删除人：{}", id, updatedBy);
    }

    @Override
    public MessageForwardingConfigVO get(Long id) {
        AmfMessageForwardingConfigDO messageForwardingConfigDO = forwardingConfigDAO.get(id);
        return toVO(messageForwardingConfigDO);
    }

    @Override
    public int count(Integer messageId) {
        return forwardingConfigDAO.count(messageId);
    }

    @Override
    public List<MessageForwardingConfigVO> list(Integer messageId) {
        List<AmfMessageForwardingConfigDO> forwardingConfigDOList = forwardingConfigDAO.list(messageId);
        if (forwardingConfigDOList == null || forwardingConfigDOList.isEmpty()) {
            return Collections.emptyList();
        }
        return forwardingConfigDOList.parallelStream().map(e -> toVO(e)).collect(Collectors.toList());
    }

    @Override
    public List<MessageForwardingConfigDTO> list4Process(Integer messageId) {
        List<AmfMessageForwardingConfigDO> forwardingConfigDOList = forwardingConfigDAO.list(messageId);
        if (forwardingConfigDOList == null || forwardingConfigDOList.isEmpty()) {
            return Collections.emptyList();
        }
        return forwardingConfigDOList.parallelStream().map(e -> toDTO(e)).collect(Collectors.toList());
    }

    /**
     * 转换为DO对象
     *
     * @param messageForwardingConfigVO
     * @return
     */
    private AmfMessageForwardingConfigDO toDO(MessageForwardingConfigVO messageForwardingConfigVO) {
        if (messageForwardingConfigVO == null) {
            return null;
        }
        AmfMessageForwardingConfigDO messageForwardingConfigDO = new AmfMessageForwardingConfigDO();
        messageForwardingConfigDO.setId(messageForwardingConfigVO.getId());
        messageForwardingConfigDO.setMessageId(messageForwardingConfigVO.getMessageId());
        messageForwardingConfigDO.setTargetSystem(messageForwardingConfigVO.getTargetSystem());
        messageForwardingConfigDO.setForwardingWay(messageForwardingConfigVO.getForwardingWay());
        messageForwardingConfigDO.setTargetAddress(messageForwardingConfigVO.getTargetAddress());
        messageForwardingConfigDO.setRetryTimes(messageForwardingConfigVO.getRetryTimes());
        messageForwardingConfigDO.setCallbackRequired(messageForwardingConfigVO.getCallbackRequired());
        messageForwardingConfigDO.setCreatedBy(messageForwardingConfigVO.getCreatedBy());
        messageForwardingConfigDO.setCreateTime(messageForwardingConfigVO.getCreateTime());
        messageForwardingConfigDO.setUpdatedBy(messageForwardingConfigVO.getUpdatedBy());
        messageForwardingConfigDO.setUpdateTime(messageForwardingConfigVO.getUpdateTime());
        return messageForwardingConfigDO;
    }

    /**
     * 转换为VO对象
     *
     * @param messageForwardingConfigDO
     * @return
     */
    private MessageForwardingConfigVO toVO(AmfMessageForwardingConfigDO messageForwardingConfigDO) {
        if (messageForwardingConfigDO == null) {
            return null;
        }
        MessageForwardingConfigVO messageForwardingConfigVO = new MessageForwardingConfigVO();
        messageForwardingConfigVO.setId(messageForwardingConfigDO.getId());
        messageForwardingConfigVO.setMessageId(messageForwardingConfigDO.getMessageId());
        messageForwardingConfigVO.setTargetSystem(messageForwardingConfigDO.getTargetSystem());
        messageForwardingConfigVO.setForwardingWay(messageForwardingConfigDO.getForwardingWay());
        messageForwardingConfigVO.setTargetAddress(messageForwardingConfigDO.getTargetAddress());
        messageForwardingConfigVO.setRetryTimes(messageForwardingConfigDO.getRetryTimes());
        messageForwardingConfigVO.setCallbackRequired(messageForwardingConfigDO.getCallbackRequired());
        messageForwardingConfigVO.setCreatedBy(messageForwardingConfigDO.getCreatedBy());
        messageForwardingConfigVO.setCreateTime(messageForwardingConfigDO.getCreateTime());
        messageForwardingConfigVO.setUpdatedBy(messageForwardingConfigDO.getUpdatedBy());
        messageForwardingConfigVO.setUpdateTime(messageForwardingConfigDO.getUpdateTime());
        return messageForwardingConfigVO;
    }

    /**
     * 转换为DTO对象
     *
     * @param messageForwardingConfigDO
     * @return
     */
    private MessageForwardingConfigDTO toDTO(AmfMessageForwardingConfigDO messageForwardingConfigDO) {
        if (messageForwardingConfigDO == null) {
            return null;
        }
        MessageForwardingConfigDTO messageForwardingConfigDTO = new MessageForwardingConfigDTO();
        messageForwardingConfigDTO.setId(messageForwardingConfigDO.getId());
        messageForwardingConfigDTO.setMessageId(messageForwardingConfigDO.getMessageId());
        messageForwardingConfigDTO.setTargetSystem(messageForwardingConfigDO.getTargetSystem());
        messageForwardingConfigDTO.setForwardingWay(messageForwardingConfigDO.getForwardingWay());
        messageForwardingConfigDTO.setTargetAddress(messageForwardingConfigDO.getTargetAddress());
        messageForwardingConfigDTO.setRetryTimes(messageForwardingConfigDO.getRetryTimes());
        messageForwardingConfigDTO.setCallbackRequired(messageForwardingConfigDO.getCallbackRequired());
        return messageForwardingConfigDTO;
    }

    /**
     * 清除缓存
     *
     * @param messageId 消息ID
     */
    private void evictCache(Integer messageId) {
        // 清除缓存
        String cacheKey = MessageConstant.MESSAGE_CONFIG_CACHE_NAME + "::" + messageId;
        if (redisTemplate.hasKey(cacheKey)) {
            redisTemplate.delete(cacheKey);
        }
    }
}
