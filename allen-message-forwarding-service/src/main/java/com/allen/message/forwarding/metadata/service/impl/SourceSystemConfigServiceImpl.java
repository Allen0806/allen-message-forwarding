package com.allen.message.forwarding.metadata.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allen.message.forwarding.metadata.dao.SourceSystemConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfBusinessLineConfigDTO;
import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;
import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDTO;
import com.allen.message.forwarding.metadata.service.BusinessLineConfigService;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.tool.string.StringUtil;

/**
 * 消息来源系统配置信息管理Service层接口实现类
 *
 * @author Allen
 * @date Jul 20, 2020
 * @since 1.0.0
 */
@Service
public class SourceSystemConfigServiceImpl implements SourceSystemConfigService {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SourceSystemConfigServiceImpl.class);

	/**
	 * 业务线信息服务
	 */
	@Autowired
	private BusinessLineConfigService businessLineConfigService;

	/**
	 * 消息来源系统配置信息DAO层接口实例
	 */
	@Autowired
	private SourceSystemConfigDAO sourceSystemConfigDAO;

	@Transactional
	@Override
	public void save(AmfSourceSystemConfigDTO sourceSystemConfigDTO) {
		AmfSourceSystemConfigDO sourceSystemConfigDO = toDO(sourceSystemConfigDTO);
		sourceSystemConfigDO.setDeleted(0);
		LocalDateTime now = LocalDateTime.now();
		sourceSystemConfigDO.setCreateTime(now);
		sourceSystemConfigDO.setUpdateTime(now);
		if (StringUtil.isBlank(sourceSystemConfigDO.getUpdatedBy())) {
			sourceSystemConfigDO.setUpdatedBy(sourceSystemConfigDO.getCreatedBy());
		}
		sourceSystemConfigDAO.save(sourceSystemConfigDO);
		LOGGER.info("保存消息来源系统配置信息成功，来源系统名称：{}", sourceSystemConfigDO.getSourceSystemName());
	}

	@Transactional
	@Override
	public void update(AmfSourceSystemConfigDTO sourceSystemConfigDTO) {
		AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(sourceSystemConfigDTO.getId());
		if (sourceSystemConfigDO == null) {
			LOGGER.info("未查到对应的来源系统信息，来源系统主键：{}", sourceSystemConfigDTO.getId());
			return;
		}
		if (sourceSystemConfigDO.getSourceSystemName().equals(sourceSystemConfigDTO.getSourceSystemName())) {
			LOGGER.info("来源系统名称没有变化，不进行更新操作，来源系统名称：{}", sourceSystemConfigDTO.getSourceSystemName());
			return;
		}
		sourceSystemConfigDO.setSourceSystemName(sourceSystemConfigDTO.getSourceSystemName());
		sourceSystemConfigDO.setUpdatedBy(sourceSystemConfigDO.getUpdatedBy());
		sourceSystemConfigDO.setUpdateTime(LocalDateTime.now());
		sourceSystemConfigDAO.update(sourceSystemConfigDO);
		// TODO 更新消息信息的来源系统名称

		LOGGER.info("更新消息来源系统配置信息成功，来源系统名称：{}", sourceSystemConfigDO.getSourceSystemName());
	}

	@Transactional
	@Override
	public void remove(Long id, String updatedBy) {
		// TODO 查询该消息来源系统是否有关联的有效的消息配置信息，如果有则不允许更新

		AmfSourceSystemConfigDO sourceSystemConfigDO = new AmfSourceSystemConfigDO();
		sourceSystemConfigDO.setId(id);
		sourceSystemConfigDO.setDeleted(1);
		sourceSystemConfigDO.setUpdatedBy(updatedBy);
		sourceSystemConfigDO.setUpdateTime(LocalDateTime.now());
		sourceSystemConfigDAO.update(sourceSystemConfigDO);
		LOGGER.info("删除消息来源系统配置信息成功，ID：{}，修改人：{}", id, updatedBy);
	}

	@Override
	public AmfSourceSystemConfigDTO get(Long id) {
		AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(id);
		if (sourceSystemConfigDO == null) {
			return null;
		}
		AmfBusinessLineConfigDTO businessLineConfigDTO = businessLineConfigService
				.get(sourceSystemConfigDO.getBusinessLineConfigId());
		if (businessLineConfigDTO == null) {
			// 如果所属业务线为空，则返回null
			return null;
		}
		return toDTO(sourceSystemConfigDO, businessLineConfigDTO);
	}

	@Override
	public int count(Long businessLineConfigId) {
		return sourceSystemConfigDAO.count(businessLineConfigId);
	}

	@Override
	public List<AmfSourceSystemConfigDTO> list4Paging(Long businessLineConfigId, int pageNo, int pageSize) {
		AmfBusinessLineConfigDTO businessLineConfigDTO = businessLineConfigService.get(businessLineConfigId);
		if (businessLineConfigDTO == null) {
			return Collections.emptyList();
		}
		int startNo = (pageNo - 1) * pageSize;
		List<AmfSourceSystemConfigDO> sourceSystemConfigDOList = sourceSystemConfigDAO.list4Paging(businessLineConfigId,
				startNo, pageSize);
		if (sourceSystemConfigDOList == null || sourceSystemConfigDOList.isEmpty()) {
			return Collections.emptyList();
		}
		return sourceSystemConfigDOList.stream().map(e -> toDTO(e, businessLineConfigDTO)).collect(Collectors.toList());
	}

	/**
	 * 将DTO对象转换为DO对象
	 * 
	 * @param sourceSystemConfigDTO DTO对象
	 * @return DO对象
	 */
	private AmfSourceSystemConfigDO toDO(AmfSourceSystemConfigDTO sourceSystemConfigDTO) {
		if (sourceSystemConfigDTO == null) {
			return null;
		}
		AmfSourceSystemConfigDO sourceSystemConfigDO = new AmfSourceSystemConfigDO();
		sourceSystemConfigDO.setId(sourceSystemConfigDTO.getId());
		sourceSystemConfigDO.setBusinessLineConfigId(sourceSystemConfigDTO.getBusinessLineConfigId());
		sourceSystemConfigDO.setSourceSystemId(sourceSystemConfigDTO.getSourceSystemId());
		sourceSystemConfigDO.setSourceSystemName(sourceSystemConfigDTO.getSourceSystemName());
		sourceSystemConfigDO.setCreatedBy(sourceSystemConfigDTO.getCreatedBy());
		sourceSystemConfigDO.setCreateTime(sourceSystemConfigDTO.getCreateTime());
		sourceSystemConfigDO.setUpdatedBy(sourceSystemConfigDTO.getUpdatedBy());
		sourceSystemConfigDO.setUpdateTime(sourceSystemConfigDTO.getUpdateTime());
		return sourceSystemConfigDO;
	}

	/**
	 * 将DO对象转换为DTO对象
	 * 
	 * @param sourceSystemConfigDTO DO对象
	 * @param businessLineConfigDTO 业务线信息对象
	 * @return DTO对象
	 */
	private AmfSourceSystemConfigDTO toDTO(AmfSourceSystemConfigDO sourceSystemConfigDO,
			AmfBusinessLineConfigDTO businessLineConfigDTO) {
		if (sourceSystemConfigDO == null) {
			return null;
		}
		AmfSourceSystemConfigDTO sourceSystemConfigDTO = new AmfSourceSystemConfigDTO();
		sourceSystemConfigDTO.setId(sourceSystemConfigDO.getId());
		sourceSystemConfigDTO.setBusinessLineConfigId(sourceSystemConfigDO.getBusinessLineConfigId());
		sourceSystemConfigDTO.setSourceSystemId(sourceSystemConfigDO.getSourceSystemId());
		sourceSystemConfigDTO.setSourceSystemName(sourceSystemConfigDO.getSourceSystemName());
		sourceSystemConfigDTO.setCreatedBy(sourceSystemConfigDO.getCreatedBy());
		sourceSystemConfigDTO.setCreateTime(sourceSystemConfigDO.getCreateTime());
		sourceSystemConfigDTO.setUpdatedBy(sourceSystemConfigDO.getUpdatedBy());
		sourceSystemConfigDTO.setUpdateTime(sourceSystemConfigDO.getUpdateTime());
		if (businessLineConfigDTO != null) {
			sourceSystemConfigDTO.setBusinessLineId(businessLineConfigDTO.getBusinessLineId());
			sourceSystemConfigDTO.setBusinessLineName(businessLineConfigDTO.getBusinessLineName());
		}
		return sourceSystemConfigDTO;
	}

}
