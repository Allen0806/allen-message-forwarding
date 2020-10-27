package com.allen.message.forwarding.metadata.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allen.message.forwarding.metadata.constant.CacheNameConstant;
import com.allen.message.forwarding.metadata.dao.MessageConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfMessageConfigDO;
import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageConfigVO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.metadata.service.MessageForwardingConfigService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.string.StringUtil;

/**
 * 消息配置管理服务层接口实现类，缓存直接使用redis版本
 *
 * @author Allen
 * @date 2020年10月19日
 * @since 1.0.0
 */
@Service
public class MessageConfigServiceImpl implements MessageConfigService {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageConfigServiceImpl.class);

	/**
	 * DAO层实例
	 */
	@Autowired
	private MessageConfigDAO messageConfigDAO;

	/**
	 * 消息转发服务层实例
	 */
	@Autowired
	private MessageForwardingConfigService messageForwardingConfigService;

	/**
	 * redisTemplate实例
	 */
	@Autowired
	private RedisTemplate<String, MessageConfigDTO> redisTemplate;

	/**
	 * Redisson客户端实例
	 */
	@Autowired
	private RedissonClient redissonClient;

	@Transactional
	@Override
	public void save(MessageConfigVO messageConfigVO) {
		AmfMessageConfigDO messageConfigDO = toDO(messageConfigVO);
		messageConfigDO.setDeleted(0);
		LocalDateTime now = LocalDateTime.now();
		messageConfigDO.setCreateTime(now);
		messageConfigDO.setUpdateTime(now);
		if (StringUtil.isBlank(messageConfigDO.getUpdatedBy())) {
			messageConfigDO.setUpdatedBy(messageConfigDO.getCreatedBy());
		}
		messageConfigDAO.save(messageConfigDO);
		LOGGER.info("保存消息配置信息成功，消息名称：{}", messageConfigDO.getMessageName());
	}

	@Transactional
	@Override
	public void update(MessageConfigVO messageConfigVO) {
		AmfMessageConfigDO messageConfigDO = messageConfigDAO.get(messageConfigVO.getId());
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
			messageConfigDO.setMessageStatus(messageConfigVO.getMessageStatus());
			isNotChanged = false;
		}
		if (isNotChanged) {
			LOGGER.info("消息配置信息没有变化，不进行更新操作，消息主键：{}", messageConfigVO.getId());
			return;
		}
		messageConfigDO.setUpdatedBy(messageConfigVO.getUpdatedBy());
		messageConfigDAO.update(messageConfigDO);
		LOGGER.info("更新消息配置信息成功，消息名称：{}", messageConfigDO.getMessageName());
		// 清除缓存
		evictCache(messageConfigDO);
	}

	@Transactional
	@Override
	public void updateBusinessLineName(String businessLineId, String businessLineName) {
		int count = messageConfigDAO.updateBusinessLineName(businessLineId, businessLineName);
		if (count == 0) {
			return;
		}
//	 	清除缓存
		int pageSize = 10;
		int pageAmount = count / pageSize + 1;
		int startNo;
		for (int i = 0; i < pageAmount; i++) {
			startNo = pageSize * i;
			List<AmfMessageConfigDO> messageConfigDOList = messageConfigDAO.listByBusinessLineId4Paging(businessLineId,
					startNo, pageSize);
			messageConfigDOList.parallelStream().forEach(e -> evictCache(e));
		}
	}

	@Transactional
	@Override
	public void updateSourceSystemName(Integer sourceSystemId, String sourceSystemName) {
		int count = messageConfigDAO.updateSourceSystemName(sourceSystemId, sourceSystemName);
		if (count == 0) {
			return;
		}
		// 清除缓存
		int pageSize = 10;
		int pageAmount = count / pageSize + 1;
		int startNo;
		for (int i = 0; i < pageAmount; i++) {
			startNo = pageSize * i;
			List<AmfMessageConfigDO> messageConfigDOList = messageConfigDAO.listBySourceSystemId4Paging(sourceSystemId,
					startNo, pageSize);
			messageConfigDOList.parallelStream().forEach(e -> evictCache(e));
		}
	}

	@Transactional
	@Override
	public void remove(Integer messageId, String updatedBy) {
		AmfMessageConfigDO messageConfigDO = messageConfigDAO.getByMessageId(messageId);
		if (messageConfigDO == null) {
			return;
		}
		int messageForwardingConfigCount = messageForwardingConfigService.count(messageId);
		if (messageForwardingConfigCount > 0) {
			throw new CustomBusinessException("MF0004", "存在有效的消息转发信息，不能删除");
		}
		messageConfigDO.setDeleted(1);
		messageConfigDO.setUpdatedBy(updatedBy);
		messageConfigDAO.update(messageConfigDO);
		evictCache(messageConfigDO);
	}

	@Override
	public MessageConfigVO get(Long id) {
		AmfMessageConfigDO messageConfigDO = messageConfigDAO.get(id);
		return toVO(messageConfigDO);
	}

	@Override
	public int countBySourceSystemId(Integer sourceSystemId) {
		return messageConfigDAO.countBySourceSystemId(sourceSystemId);
	}

	@Override
	public List<MessageConfigVO> listBySourceSystemId4Paging(Integer sourceSystemId, int pageNo, int pageSize) {
		if (pageNo < 1 || pageSize < 1) {
			throw new CustomBusinessException("MF0003", "当前页数或每页行数不能小于1");
		}
		int startNo = (pageNo - 1) * pageSize;
		List<AmfMessageConfigDO> messageConfigDOList = messageConfigDAO.listBySourceSystemId4Paging(sourceSystemId,
				startNo, pageSize);
		if (messageConfigDOList == null || messageConfigDOList.isEmpty()) {
			return Collections.emptyList();
		}
		return messageConfigDOList.parallelStream().map(e -> toVO(e)).collect(Collectors.toList());
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
		String lockKey = CacheNameConstant.MESSAGE_CONFIG_LOCK_NAME + "::" + messageId;
		RLock lock = redissonClient.getLock(lockKey);
		try {
			if (lock.tryLock(5, 5, TimeUnit.SECONDS)) {
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
					messageConfigDTO = toDTO(messageConfigDO);
					messageConfigDTO.setForwardingConfigs(forwardingConfigs);
					cacheable(messageConfigDTO);
					return messageConfigDTO;
				} catch (Exception e) {
					LOGGER.error("获取消息配置信息异常，消息ID：{}", messageId, e);
					throw new CustomBusinessException("MF0005", "获取消息配置信息异常", e);
				} finally {
					lock.unlock();
				}
			}
		} catch (Exception e) {
			LOGGER.error("锁处理异常，消息ID：{}", messageId, e);
			throw new CustomBusinessException("MF0005", "获取消息配置信息异常", e);
		}
		return null;
	}

	/**
	 * 将VO对象转换为DO对象
	 * 
	 * @param messageConfigVO VO对象
	 * @return DO对象
	 */
	private AmfMessageConfigDO toDO(MessageConfigVO messageConfigVO) {
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
	private MessageConfigVO toVO(AmfMessageConfigDO messageConfigDO) {
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
	private MessageConfigDTO toDTO(AmfMessageConfigDO messageConfigDO) {
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
		String cacheKey = CacheNameConstant.MESSAGE_CONFIG_CACHE_NAME + "::" + messageConfigDO.getMessageId();
		if (redisTemplate.hasKey(cacheKey)) {
			redisTemplate.delete(cacheKey);
		}
	}

	/**
	 * 从缓存中获取消息配置信息
	 * 
	 * @param messageId 消息ID
	 * @return
	 */
	private MessageConfigDTO getFromCache(Integer messageId) {
		String cacheKey = CacheNameConstant.MESSAGE_CONFIG_CACHE_NAME + "::" + messageId;
		if (redisTemplate.hasKey(cacheKey)) {
			MessageConfigDTO messageConfigDTO = redisTemplate.opsForValue().get(cacheKey);
			if (messageConfigDTO != null) {
				return messageConfigDTO;
			}
		}
		return null;
	}

	/**
	 * 将消息配置信息设置到缓存中
	 * 
	 * @param messageConfigDTO
	 */
	private void cacheable(MessageConfigDTO messageConfigDTO) {
		String cacheKey = CacheNameConstant.MESSAGE_CONFIG_CACHE_NAME + "::" + messageConfigDTO.getMessageId();
		if (!redisTemplate.hasKey(cacheKey)) {
			redisTemplate.opsForValue().set(cacheKey, messageConfigDTO);
		}
	}

}
