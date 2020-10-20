package com.allen.message.forwarding.metadata.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;

import com.allen.message.forwarding.metadata.constant.CacheNameConstant;
import com.allen.message.forwarding.metadata.dao.MessageConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfMessageConfigDO;
import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageConfigVO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.metadata.service.MessageForwardingConfigService;
import com.allen.tool.string.StringUtil;

/**
 * 消息配置管理服务层接口实现类
 *
 * @author Allen
 * @date 2020年10月19日
 * @since 1.0.0
 */
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

	@CacheEvict(cacheNames = CacheNameConstant.MESSAGE_CONFIG_CACHE_NAME, key = "#messageConfigVO.messageId")
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
		// TODO 清除缓存
	}

	@Override
	public void updateBusinessLineName(String businessLineId, String businessLineName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSourceSystemName(Integer sourceSystemId, String sourceSystemName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Long id, String updatedBy) {
		// TODO Auto-generated method stub

	}

	@Override
	public MessageConfigVO get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count(Integer sourceSystemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MessageConfigVO> list4Paging(Integer sourceSystemId, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * redis中缓存的key为：cacheNames::key
	 */
	@Cacheable(cacheNames = CacheNameConstant.MESSAGE_CONFIG_CACHE_NAME, key = "#messageId")
	@Override
	public MessageConfigDTO getByMessageId(Integer messageId) {
		// TODO Auto-generated method stub
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

}
