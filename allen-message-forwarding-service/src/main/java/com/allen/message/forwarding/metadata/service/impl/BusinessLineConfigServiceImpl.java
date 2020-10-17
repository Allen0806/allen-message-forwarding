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

import com.allen.message.forwarding.metadata.dao.BusinessLineConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfBusinessLineConfigDO;
import com.allen.message.forwarding.metadata.model.AmfBusinessLineConfigDTO;
import com.allen.message.forwarding.metadata.service.BusinessLineConfigService;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.string.StringUtil;

/**
 * 消息所属业务线配置信息管理Service实现类
 *
 * @author Allen
 * @date 2020年10月16日
 * @since 1.0.0
 */
@Service
public class BusinessLineConfigServiceImpl implements BusinessLineConfigService {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BusinessLineConfigServiceImpl.class);

	@Autowired
	private SourceSystemConfigService sourceSystemConfigService;

	/**
	 * 消息来源系统配置信息DAO层接口实例
	 */
	@Autowired
	private BusinessLineConfigDAO businessLineConfigDAO;

	@Transactional
	@Override
	public void save(AmfBusinessLineConfigDTO businessLineConfigDTO) {
		AmfBusinessLineConfigDO businessLineConfigDO = toDO(businessLineConfigDTO);
		businessLineConfigDO.setDeleted(0);
		LocalDateTime now = LocalDateTime.now();
		businessLineConfigDO.setCreateTime(now);
		businessLineConfigDO.setUpdateTime(now);
		if (StringUtil.isBlank(businessLineConfigDO.getUpdatedBy())) {
			businessLineConfigDO.setUpdatedBy(businessLineConfigDO.getCreatedBy());
		}
		businessLineConfigDAO.save(businessLineConfigDO);
		LOGGER.info("保存消息所属业务线配置信息成功，业务线名称：{}，创建人：{}", businessLineConfigDTO.getBusinessLineName(),
				businessLineConfigDTO.getCreatedBy());
	}

	@Transactional
	@Override
	public void update(AmfBusinessLineConfigDTO businessLineConfigDTO) {
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(businessLineConfigDTO.getId());
		if (businessLineConfigDO.getBusinessLineName().equals(businessLineConfigDTO.getBusinessLineName())) {
			LOGGER.info("业务线名称没有变化，不进行消息所属业务线配置信息更新操作，业务线名称：{}", businessLineConfigDTO.getBusinessLineName());
			return;
		}
		businessLineConfigDO.setBusinessLineName(businessLineConfigDTO.getBusinessLineName());
		businessLineConfigDO.setUpdatedBy(businessLineConfigDTO.getUpdatedBy());
		businessLineConfigDO.setUpdateTime(LocalDateTime.now());
		businessLineConfigDAO.update(businessLineConfigDO);
		// TODO 更新消息配置信息的业务线名称

		LOGGER.info("更新消息所属业务线配置信息成功，业务线名称：{}，修改人：{}", businessLineConfigDTO.getBusinessLineName(),
				businessLineConfigDTO.getUpdatedBy());
	}

	@Transactional
	@Override
	public void remove(Long id, String updatedBy) {
		// 判断是否存在关联的未标记为删除的来源系统信息，如果存在，则不允许删除
		int sourceSystemAmount = sourceSystemConfigService.count(id);
		if (sourceSystemAmount > 0) {
			LOGGER.info("存在未删除的来源系统信息，不能进行业务线配置信息删除操作，业务线主键：{}", id);
			throw new CustomBusinessException("MF0101", "存在未删除的来源系统信息，不能进行业务线配置信息删除操作");
		}
		AmfBusinessLineConfigDO businessLineConfigDO = new AmfBusinessLineConfigDO();
		businessLineConfigDO.setId(id);
		businessLineConfigDO.setDeleted(1);
		businessLineConfigDO.setUpdatedBy(updatedBy);
		businessLineConfigDAO.update(businessLineConfigDO);
		LOGGER.info("删除消息所属业务线配置信息成功，业务线主键：{}，删除人{}", id, updatedBy);
	}

	@Override
	public AmfBusinessLineConfigDTO get(Long id) {
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(id);
		return toDTO(businessLineConfigDO);
	}

	@Override
	public AmfBusinessLineConfigDTO getByBusinessLineId(String businessLineId) {
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.getByBusinessLineId(businessLineId);
		return toDTO(businessLineConfigDO);
	}

	@Override
	public List<AmfBusinessLineConfigDTO> list4Fuzzy(String businessLineId, String businessLineName) {
		// TODO Auto-generated method stub
		if (StringUtil.isBlank(businessLineId) && StringUtil.isBlank(businessLineName)) {
			LOGGER.info("业务线ID和业务线名称不能同时为空");
			throw new CustomBusinessException("MF0102", "业务线ID和业务线名称不能同时为空");
		}
		if (StringUtil.isNotBlank(businessLineId)) {
			businessLineId = businessLineId.trim() + "%";
		}
		if (StringUtil.isNotBlank(businessLineName)) {
			businessLineName = businessLineName.trim() + "%";
		}
		List<AmfBusinessLineConfigDO> businessLineConfigDOList = businessLineConfigDAO.list4Fuzzy(businessLineId,
				businessLineName);
		return toDTOList(businessLineConfigDOList);
	}

	@Override
	public int count() {
		return businessLineConfigDAO.count();
	}

	@Override
	public List<AmfBusinessLineConfigDTO> list4Paging(int pageNo, int pageSize) {
		int startNo = (pageNo - 1) * pageSize;
		List<AmfBusinessLineConfigDO> businessLineConfigDOList = businessLineConfigDAO.list4Paging(startNo, pageSize);
		return toDTOList(businessLineConfigDOList);
	}

	/**
	 * 将DTO对象转换为DO对象
	 * 
	 * @param businessLineConfigDTO DTO对象
	 * @return DO对象
	 */
	private AmfBusinessLineConfigDO toDO(AmfBusinessLineConfigDTO businessLineConfigDTO) {
		if (businessLineConfigDTO == null) {
			return null;
		}
		AmfBusinessLineConfigDO businessLineConfigDO = new AmfBusinessLineConfigDO();
		businessLineConfigDO.setId(businessLineConfigDTO.getId());
		businessLineConfigDO.setBusinessLineId(businessLineConfigDTO.getBusinessLineId());
		businessLineConfigDO.setBusinessLineName(businessLineConfigDTO.getBusinessLineName());
		businessLineConfigDO.setCreatedBy(businessLineConfigDTO.getCreatedBy());
		businessLineConfigDO.setCreateTime(businessLineConfigDTO.getCreateTime());
		businessLineConfigDO.setUpdatedBy(businessLineConfigDTO.getUpdatedBy());
		businessLineConfigDO.setUpdateTime(businessLineConfigDTO.getUpdateTime());
		return businessLineConfigDO;
	}

	/**
	 * 将DO对象转换为DTO对象
	 * 
	 * @param businessLineConfigDO DO对象
	 * @return DTO对象
	 */
	private AmfBusinessLineConfigDTO toDTO(AmfBusinessLineConfigDO businessLineConfigDO) {
		if (businessLineConfigDO == null) {
			return null;
		}
		AmfBusinessLineConfigDTO businessLineConfigDTO = new AmfBusinessLineConfigDTO();
		businessLineConfigDTO.setId(businessLineConfigDO.getId());
		businessLineConfigDTO.setBusinessLineId(businessLineConfigDO.getBusinessLineId());
		businessLineConfigDTO.setBusinessLineName(businessLineConfigDO.getBusinessLineName());
		businessLineConfigDTO.setCreatedBy(businessLineConfigDO.getCreatedBy());
		businessLineConfigDTO.setCreateTime(businessLineConfigDO.getCreateTime());
		businessLineConfigDTO.setUpdatedBy(businessLineConfigDO.getUpdatedBy());
		businessLineConfigDTO.setUpdateTime(businessLineConfigDO.getUpdateTime());
		return businessLineConfigDTO;
	}

	/**
	 * 将DO列表转换为DTO列表
	 * 
	 * @param businessLineConfigDOList DO列表
	 * @return DTO列表
	 */
	private List<AmfBusinessLineConfigDTO> toDTOList(List<AmfBusinessLineConfigDO> businessLineConfigDOList) {
		if (businessLineConfigDOList == null || businessLineConfigDOList.isEmpty()) {
			return Collections.emptyList();
		}
		return businessLineConfigDOList.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

}
