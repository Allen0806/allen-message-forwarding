package com.allen.message.forwarding.metadata.service.impl;

import com.allen.message.forwarding.constant.MessageConstant;
import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.metadata.dao.MessageConfigDAO;
import com.allen.message.forwarding.metadata.model.*;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.metadata.service.MessageForwardingConfigService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.PagingQueryResult;
import com.allen.tool.string.StringUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 消息配置管理服务层接口实现类，缓存使用Redisson
 *
 * @author Allen
 * @date 2020年10月19日
 * @since 1.0.0
 */
@Service
@Primary
@RefreshScope
public class MessageConfigServiceImpl implements MessageConfigService {

    /**
     * 日志纪录器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConfigServiceImpl.class);

    /**
     * DAO层实例
     */
    @Resource
    private MessageConfigDAO messageConfigDAO;

    /**
     * 消息转发服务层实例
     */
    @Resource
    private MessageForwardingConfigService messageForwardingConfigService;

    /**
     * Redisson客户端实例
     */
    @Resource
    private RedissonClient redissonClient;

    @Transactional
    @Override
    public void save(MessageConfigVO messageConfigVO) {
        AmfMessageConfigDO messageConfigDO = toDataObject(messageConfigVO);
        if (StringUtil.isBlank(messageConfigDO.getUpdatedBy())) {
            messageConfigDO.setUpdatedBy(messageConfigDO.getCreatedBy());
        }
        int count = messageConfigDAO.save(messageConfigDO);
        if (count == 0) {
            LOGGER.error("保存消息配置信失败，消息名称：{}，创建人：{}", messageConfigDO.getMessageName(), messageConfigDO.getCreatedBy());
            throw new CustomBusinessException(ResultStatuses.MF_0301);
        }
        LOGGER.info("保存消息配置信成功，消息名称：{}，创建人：{}", messageConfigDO.getMessageName(), messageConfigDO.getCreatedBy());
    }

    @Transactional
    @Override
    public void update(MessageConfigVO messageConfigVO) {
        AmfMessageConfigDO messageConfigDO = messageConfigDAO.get(messageConfigVO.getId());
        if (messageConfigDO == null) {
            LOGGER.error("不存在对应的消息配置信息，消息配置主键：{}", messageConfigVO.getId());
            throw new CustomBusinessException(ResultStatuses.MF_0302);
        }
        boolean isNotChanged = true;
        if (StringUtil.isNotBlank(messageConfigVO.getMessageName())
                && !messageConfigDO.getMessageName().equals(messageConfigVO.getMessageName())) {
            messageConfigDO.setMessageName(messageConfigVO.getMessageName());
            isNotChanged = false;
        } else if (messageConfigVO.getMessageStatus() != null
                && messageConfigDO.getMessageStatus() != messageConfigVO.getMessageStatus()) {
            messageConfigDO.setMessageStatus(messageConfigVO.getMessageStatus());
            isNotChanged = false;
        } else if (StringUtil.isNotBlank(messageConfigVO.getCallbackUrl())
                && !messageConfigDO.getCallbackUrl().equals(messageConfigVO.getCallbackUrl())) {
            messageConfigDO.setCallbackUrl(messageConfigVO.getCallbackUrl());
            isNotChanged = false;
        }
        String updatedBy = messageConfigVO.getUpdatedBy();
        if (isNotChanged) {
            LOGGER.info("消息配置信息没有变化，不进行更新操作，消息名称：{}，修改人：{}", messageConfigDO.getMessageName(), updatedBy);
            return;
        }
        // 清除缓存
        evictCache(messageConfigDO);
        messageConfigDO.setUpdatedBy(messageConfigVO.getUpdatedBy());
        int count = messageConfigDAO.update(messageConfigDO);
        if (count == 0) {
            LOGGER.error("更新消息配置信失败，消息ID：{}，修改人：{}", messageConfigDO.getId(), updatedBy);
            throw new CustomBusinessException(ResultStatuses.MF_0303);
        }
        LOGGER.info("更新消息配置信息成功，消息名称：{}，修改人：{}", messageConfigDO.getMessageName(), updatedBy);
    }

    @Transactional
    @Override
    public void updateBusinessLineName(String businessLineId, String businessLineName, String updatedBy) {
        MessageConfigQueryParamDTO queryParam = new MessageConfigQueryParamDTO();
        queryParam.setBusinessLineId(businessLineId);
        Integer count = messageConfigDAO.count(queryParam);
        if (count == 0) {
            return;
        }

        // 清除缓存
        int pageSize = 50;
        int pageAmount = count / pageSize + 1;
        int startNo;
        PagingQueryParam<MessageConfigQueryParamDTO> pagingQueryParam = new PagingQueryParam<>(queryParam, 0, pageSize);
        for (int i = 0; i < pageAmount; i++) {
            startNo = pageSize * i;
            pagingQueryParam.setStartNo(startNo);
            List<AmfMessageConfigDO> messageConfigDOList = messageConfigDAO.list4Paging(pagingQueryParam);
            if (messageConfigDOList == null || messageConfigDOList.isEmpty()) {
                continue;
            }
            messageConfigDOList.parallelStream().forEach(e -> evictCache(e));
        }
        messageConfigDAO.updateBusinessLineName(businessLineId, businessLineName, updatedBy);
        LOGGER.info("更新消息配置信息业务线名称成功，业务线名称：{}，更新数量：{}，修改人：{}", businessLineName, count, updatedBy);
    }

    @Transactional
    @Override
    public void updateSourceSystemName(Integer sourceSystemId, String sourceSystemName, String updatedBy) {
        MessageConfigQueryParamDTO queryParam = new MessageConfigQueryParamDTO();
        queryParam.setSourceSystemId(sourceSystemId);
        Integer count = messageConfigDAO.count(queryParam);
        if (count == 0) {
            return;
        }

        // 清除缓存
        int pageSize = 50;
        int pageAmount = count / pageSize + 1;
        int startNo;
        PagingQueryParam<MessageConfigQueryParamDTO> pagingQueryParam = new PagingQueryParam<>(queryParam, 0, pageSize);
        for (int i = 0; i < pageAmount; i++) {
            startNo = pageSize * i;
            pagingQueryParam.setStartNo(startNo);
            List<AmfMessageConfigDO> messageConfigDOList = messageConfigDAO.list4Paging(pagingQueryParam);
            if (messageConfigDOList == null || messageConfigDOList.isEmpty()) {
                continue;
            }
            messageConfigDOList.parallelStream().forEach(e -> evictCache(e));
        }
        messageConfigDAO.updateSourceSystemName(sourceSystemId, sourceSystemName, updatedBy);
        LOGGER.info("更新消息配置信息来源系统名称成功，来源系统名称：{}，更新数量：{}，修改人：{}", sourceSystemName, count, updatedBy);
    }

    @Transactional
    @Override
    public void remove(Integer messageId, String updatedBy) {
        AmfMessageConfigDO messageConfigDO = messageConfigDAO.getByMessageId(messageId);
        if (messageConfigDO == null) {
            LOGGER.error("不存在对应的消息配置信息，消息ID：{}", messageId);
            throw new CustomBusinessException(ResultStatuses.MF_0302);
        }
        evictCache(messageConfigDO);
        String messageName = messageConfigDO.getMessageName();
        // TODO 需要修改
        int messageForwardingConfigCount = messageForwardingConfigService.count(messageId);
        if (messageForwardingConfigCount > 0) {
            LOGGER.error("存在消息转发信息，不能进行消息配置信息删除操作，消息名称：{}", messageName);
            throw new CustomBusinessException(ResultStatuses.MF_0304);
        }
        messageConfigDO.setDeleted(1);
        messageConfigDO.setUpdatedBy(updatedBy);
        int count = messageConfigDAO.update(messageConfigDO);
        if (count == 0) {
            LOGGER.error("删除消息配置信息失败，消息名称：{}，删除人：{}", messageName, updatedBy);
            throw new CustomBusinessException(ResultStatuses.MF_0305);
        }
        LOGGER.info("删除消息配置信息成功，消息名称：{}，删除人：{}", messageName, updatedBy);
    }

    @Override
    public MessageConfigVO get(Long id) {
        AmfMessageConfigDO messageConfigDO = messageConfigDAO.get(id);
        return toViewObject(messageConfigDO);
    }

    /**
     * redis中缓存的key为：cacheNames::key
     */
    @Override
    public MessageConfigDTO getByMessageId(Integer messageId) {
        // 优先从缓存里获取
        MessageConfigDTO messageConfigDTO = getFromCache(messageId);
        if (messageConfigDTO != null) {
            return messageConfigDTO;
        }
        String lockKey = MessageConstant.MESSAGE_CONFIG_LOCK_NAME + ":" + messageId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(30, 30, TimeUnit.SECONDS)) {
                try {
                    // 再次从缓存里获取，二次检查
                    messageConfigDTO = getFromCache(messageId);
                    if (messageConfigDTO != null) {
                        return messageConfigDTO;
                    }

                    AmfMessageConfigDO messageConfigDO = messageConfigDAO.getByMessageId(messageId);
                    if (messageConfigDO == null) {
                        return null;
                    }
                    List<MessageForwardingConfigDTO> forwardingConfigs = messageForwardingConfigService
                            .list4Process(messageId);
                    // 如果转发信息为空，则返回null
                    if (forwardingConfigs == null || forwardingConfigs.isEmpty()) {
                        return null;
                    }
                    messageConfigDTO = toTransferObject(messageConfigDO);
                    messageConfigDTO.setForwardingConfigs(forwardingConfigs);
                    cacheable(messageConfigDTO);
                    return messageConfigDTO;
                } catch (Exception e) {
                    LOGGER.error("获取消息配置信息异常，消息ID：" + messageId, e);
                    throw new CustomBusinessException(ResultStatuses.MF_0306, e);
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            LOGGER.error("锁处理异常，消息ID：" + messageId, e);
            throw new CustomBusinessException(ResultStatuses.MF_0306, e);
        }
        return null;
    }

    @Override
    public Integer count(MessageConfigQueryParamDTO queryParam) {
        if (queryParam == null) {
            queryParam = new MessageConfigQueryParamDTO();
        }
        return messageConfigDAO.count(queryParam);
    }

    @Override
    public PagingQueryResult<MessageConfigVO> list4Paging(PagingQueryParam<MessageConfigQueryParamDTO> pagingQueryParam) {
        Integer pageNo = pagingQueryParam.getPageNo();
        if (pageNo == null) {
            pageNo = 1;
            pagingQueryParam.setPageNo(pageNo);
        }
        Integer pageSize = pagingQueryParam.getPageSize();
        if (pageSize == null) {
            pageSize = 10;
            pagingQueryParam.setPageSize(pageSize);
        }
        Integer startNo = (pageNo - 1) * pageSize;
        pagingQueryParam.setStartNo(startNo);
        MessageConfigQueryParamDTO queryParam = pagingQueryParam.getParam();
        if (queryParam == null) {
            queryParam = new MessageConfigQueryParamDTO();
            pagingQueryParam.setParam(queryParam);
        }
        Integer quantity = messageConfigDAO.count(queryParam);
        List<AmfMessageConfigDO> messageConfigDOList = messageConfigDAO.list4Paging(pagingQueryParam);
        if (messageConfigDOList == null || messageConfigDOList.isEmpty()) {
            return new PagingQueryResult<>(Collections.emptyList(), quantity, pageNo, pageSize);
        }
        List<MessageConfigVO> messageConfigVOList = messageConfigDOList.parallelStream().map(e -> toViewObject(e)).collect(Collectors.toList());
        return new PagingQueryResult<>(messageConfigVOList, quantity, pageNo, pageSize);
    }


    /**
     * 将VO对象转换为DO对象
     *
     * @param messageConfigVO VO对象
     * @return DO对象
     */
    private AmfMessageConfigDO toDataObject(MessageConfigVO messageConfigVO) {
        if (messageConfigVO == null) {
            return null;
        }
        AmfMessageConfigDO messageConfigDO = new AmfMessageConfigDO();
        messageConfigDO.setId(messageConfigVO.getId());
        messageConfigDO.setBusinessLineId(messageConfigVO.getBusinessLineId());
        messageConfigDO.setBusinessLineName(messageConfigVO.getBusinessLineName());
        messageConfigDO.setSourceSystemId(messageConfigVO.getSourceSystemId());
        messageConfigDO.setSourceSystemName(messageConfigVO.getSourceSystemName());
        messageConfigDO.setMessageId(messageConfigVO.getMessageId());
        messageConfigDO.setMessageName(messageConfigVO.getMessageName());
        messageConfigDO.setMessageStatus(messageConfigVO.getMessageStatus());
        messageConfigDO.setCallbackUrl(messageConfigVO.getCallbackUrl());
        messageConfigDO.setCreatedBy(messageConfigVO.getCreatedBy());
        messageConfigDO.setCreateTime(messageConfigVO.getCreateTime());
        messageConfigDO.setUpdatedBy(messageConfigVO.getUpdatedBy());
        messageConfigDO.setUpdateTime(messageConfigVO.getUpdateTime());
        return messageConfigDO;
    }

    /**
     * 将DO对象转换为VO对象
     *
     * @param messageConfigDO DO对象
     * @return VO对象
     */
    private MessageConfigVO toViewObject(AmfMessageConfigDO messageConfigDO) {
        if (messageConfigDO == null) {
            return null;
        }
        MessageConfigVO messageConfigVO = new MessageConfigVO();
        messageConfigVO.setId(messageConfigDO.getId());
        messageConfigVO.setBusinessLineId(messageConfigDO.getBusinessLineId());
        messageConfigVO.setBusinessLineName(messageConfigDO.getBusinessLineName());
        messageConfigVO.setSourceSystemId(messageConfigDO.getSourceSystemId());
        messageConfigVO.setSourceSystemName(messageConfigDO.getSourceSystemName());
        messageConfigVO.setMessageId(messageConfigDO.getMessageId());
        messageConfigVO.setMessageName(messageConfigDO.getMessageName());
        messageConfigVO.setMessageStatus(messageConfigDO.getMessageStatus());
        messageConfigVO.setCallbackUrl(messageConfigDO.getCallbackUrl());
        messageConfigVO.setCreatedBy(messageConfigDO.getCreatedBy());
        messageConfigVO.setCreateTime(messageConfigDO.getCreateTime());
        messageConfigVO.setUpdatedBy(messageConfigDO.getUpdatedBy());
        messageConfigVO.setUpdateTime(messageConfigDO.getUpdateTime());
        return messageConfigVO;
    }

    /**
     * 将DO对象转换为DTO对象
     *
     * @param messageConfigDO DTO对象
     * @return DTO对象
     */
    private MessageConfigDTO toTransferObject(AmfMessageConfigDO messageConfigDO) {
        if (messageConfigDO == null) {
            return null;
        }
        MessageConfigDTO messageConfigDTO = new MessageConfigDTO();
        messageConfigDTO.setBusinessLineId(messageConfigDO.getBusinessLineId());
        messageConfigDTO.setBusinessLineName(messageConfigDO.getBusinessLineName());
        messageConfigDTO.setSourceSystemId(messageConfigDO.getSourceSystemId());
        messageConfigDTO.setSourceSystemName(messageConfigDO.getSourceSystemName());
        messageConfigDTO.setMessageId(messageConfigDO.getMessageId());
        messageConfigDTO.setMessageName(messageConfigDO.getMessageName());
        messageConfigDTO.setCallbackUrl(messageConfigDO.getCallbackUrl());
        return messageConfigDTO;
    }

    /**
     * 清除缓存
     *
     * @param messageConfigDO 消息配置信息
     */
    private void evictCache(AmfMessageConfigDO messageConfigDO) {
        // 清除缓存
        String cacheKey = MessageConstant.MESSAGE_CONFIG_CACHE_NAME + ":" + messageConfigDO.getMessageId();
        RBucket<MessageConfigDTO> bucket = redissonClient.getBucket(cacheKey);
        bucket.getAndDelete();
    }

    /**
     * 从缓存中获取消息配置信息
     *
     * @param messageId 消息ID
     * @return
     */
    private MessageConfigDTO getFromCache(Integer messageId) {
        String cacheKey = MessageConstant.MESSAGE_CONFIG_CACHE_NAME + ":" + messageId;
        RBucket<MessageConfigDTO> bucket = redissonClient.getBucket(cacheKey);
        return bucket.get();
    }

    /**
     * 将消息配置信息设置到缓存中
     *
     * @param messageConfigDTO
     */
    private void cacheable(MessageConfigDTO messageConfigDTO) {
        String cacheKey = MessageConstant.MESSAGE_CONFIG_CACHE_NAME + ":" + messageConfigDTO.getMessageId();
        RBucket<MessageConfigDTO> bucket = redissonClient.getBucket(cacheKey);
        bucket.set(messageConfigDTO);
    }

}
