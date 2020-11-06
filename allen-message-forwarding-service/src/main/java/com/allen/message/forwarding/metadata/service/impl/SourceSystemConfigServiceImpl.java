package com.allen.message.forwarding.metadata.service.impl;

import static com.allen.message.forwarding.constant.StatusCodeConstant.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allen.message.forwarding.metadata.dao.SourceSystemConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;
import com.allen.message.forwarding.metadata.model.BusinessLineConfigVO;
import com.allen.message.forwarding.metadata.model.SourceSystemConfigVO;
import com.allen.message.forwarding.metadata.service.BusinessLineConfigService;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.tool.exception.CustomBusinessException;
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
	 * 消息配置管理服务实例
	 */
	@Autowired
	private MessageConfigService messageConfigService;

	/**
	 * 消息来源系统配置信息DAO层接口实例
	 */
	@Autowired
	private SourceSystemConfigDAO sourceSystemConfigDAO;

	@Transactional
	@Override
	public void save(SourceSystemConfigVO sourceSystemConfigVO) {
		AmfSourceSystemConfigDO sourceSystemConfigDO = toDO(sourceSystemConfigVO);
		if (StringUtil.isBlank(sourceSystemConfigDO.getUpdatedBy())) {
			sourceSystemConfigDO.setUpdatedBy(sourceSystemConfigDO.getCreatedBy());
		}
		int count = sourceSystemConfigDAO.save(sourceSystemConfigDO);
		if (count == 0) {
			LOGGER.error("保存来源系统信息失败，来源系统名称：{}，创建人：{}", sourceSystemConfigDO.getSourceSystemName(),
					sourceSystemConfigDO.getCreatedBy());
			throw new CustomBusinessException(MF_0201);
		}
		LOGGER.info("保存来源系统信息成功，来源系统名称：{}，创建人：{}", sourceSystemConfigDO.getSourceSystemName(),
				sourceSystemConfigDO.getCreatedBy());
	}

	@Transactional
	@Override
	public void update(SourceSystemConfigVO sourceSystemConfigVO) {
		Long id = sourceSystemConfigVO.getId();
		String newSourceSystemName = sourceSystemConfigVO.getSourceSystemName();
		AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(id);
		if (sourceSystemConfigDO == null) {
			LOGGER.error("不存在对应的来源系统信息，来源系统主键：{}", id);
			throw new CustomBusinessException(MF_0202);
		}
		if (sourceSystemConfigDO.getSourceSystemName().equals(newSourceSystemName)) {
			LOGGER.info("来源系统名称没有变化，不进行更新操作，来源系统名称：{}", newSourceSystemName);
			return;
		}
		String updatedBy = sourceSystemConfigVO.getUpdatedBy();
		Integer sourceSystemId = sourceSystemConfigDO.getSourceSystemId();
		sourceSystemConfigDO.setSourceSystemName(newSourceSystemName);
		sourceSystemConfigDO.setUpdatedBy(updatedBy);
		int count = sourceSystemConfigDAO.update(sourceSystemConfigDO);
		if (count == 0) {
			LOGGER.error("更新来源系统信息失败，来源系统ID：{}，修改人：{}", sourceSystemId, updatedBy);
			throw new CustomBusinessException(MF_0203);
		}
		// 更新消息信息的来源系统名称
		messageConfigService.updateSourceSystemName(sourceSystemId, newSourceSystemName, updatedBy);
		LOGGER.info("更新来源系统信息成功，来源系统名称：{}，修改人：{}", newSourceSystemName, updatedBy);
	}

	@Transactional
	@Override
	public void remove(Long id, String updatedBy) {
		AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(id);
		if (sourceSystemConfigDO == null) {
			LOGGER.error("不存在对应的来源系统信息，来源系统主键：{}", id);
			throw new CustomBusinessException(MF_0202);
		}
		Integer sourceSystemId = sourceSystemConfigDO.getSourceSystemId();
		String sourceSystemName = sourceSystemConfigDO.getSourceSystemName();
		// 查询该消息来源系统是否有关联的有效的消息配置信息，如果有则不允许更新
		int messageCount = messageConfigService.countBySourceSystemId(sourceSystemId);
		if (messageCount > 0) {
			LOGGER.info("存在消息配置信息，不能进行来源系统信息删除操作，来源系统名称：{}", sourceSystemName);
			throw new CustomBusinessException(MF_0204);
		}
		sourceSystemConfigDO.setDeleted(1);
		sourceSystemConfigDO.setUpdatedBy(updatedBy);
		int count = sourceSystemConfigDAO.update(sourceSystemConfigDO);
		if (count == 0) {
			LOGGER.error("删除来源系统信息失败，来源系统名称：{}，修改人：{}", sourceSystemName, updatedBy);
			throw new CustomBusinessException(MF_0205);
		}
		LOGGER.info("删除消息来源系统配置信息成功，来源系统名称：{}，修改人：{}", sourceSystemName, updatedBy);
	}

	@Override
	public SourceSystemConfigVO get(Long id) {
		AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(id);
		if (sourceSystemConfigDO == null) {
			return null;
		}
		BusinessLineConfigVO businessLineConfigDTO = businessLineConfigService
				.get(sourceSystemConfigDO.getBusinessLineConfigId());
		if (businessLineConfigDTO == null) {
			// 如果所属业务线为空，则返回null
			return null;
		}
		return toVO(sourceSystemConfigDO, businessLineConfigDTO);
	}

	@Override
	public int count(Long businessLineConfigId) {
		return sourceSystemConfigDAO.count(businessLineConfigId);
	}

	@Override
	public List<SourceSystemConfigVO> list4Paging(Long businessLineConfigId, int pageNo, int pageSize) {
		BusinessLineConfigVO businessLineConfigDTO = businessLineConfigService.get(businessLineConfigId);
		if (businessLineConfigDTO == null) {
			return Collections.emptyList();
		}
		if (pageNo < 1 || pageSize < 1) {
			throw new CustomBusinessException(MF_0001);
		}
		int startNo = (pageNo - 1) * pageSize;
		List<AmfSourceSystemConfigDO> sourceSystemConfigDOList = sourceSystemConfigDAO.list4Paging(businessLineConfigId,
				startNo, pageSize);
		if (sourceSystemConfigDOList == null || sourceSystemConfigDOList.isEmpty()) {
			return Collections.emptyList();
		}
		return sourceSystemConfigDOList.parallelStream().map(e -> toVO(e, businessLineConfigDTO))
				.collect(Collectors.toList());
	}

	/**
	 * 将VO对象转换为DO对象
	 * 
	 * @param sourceSystemConfigVO VO对象
	 * @return DO对象
	 */
	private AmfSourceSystemConfigDO toDO(SourceSystemConfigVO sourceSystemConfigVO) {
		if (sourceSystemConfigVO == null) {
			return null;
		}
		AmfSourceSystemConfigDO sourceSystemConfigDO = new AmfSourceSystemConfigDO();
		sourceSystemConfigDO.setId(sourceSystemConfigVO.getId());
		sourceSystemConfigDO.setBusinessLineConfigId(sourceSystemConfigVO.getBusinessLineConfigId());
		sourceSystemConfigDO.setSourceSystemId(sourceSystemConfigVO.getSourceSystemId());
		sourceSystemConfigDO.setSourceSystemName(sourceSystemConfigVO.getSourceSystemName());
		sourceSystemConfigDO.setCreatedBy(sourceSystemConfigVO.getCreatedBy());
		sourceSystemConfigDO.setCreateTime(sourceSystemConfigVO.getCreateTime());
		sourceSystemConfigDO.setUpdatedBy(sourceSystemConfigVO.getUpdatedBy());
		sourceSystemConfigDO.setUpdateTime(sourceSystemConfigVO.getUpdateTime());
		return sourceSystemConfigDO;
	}

	/**
	 * 将DO对象转换为VO对象
	 * 
	 * @param sourceSystemConfigDTO DO对象
	 * @param businessLineConfigVO  业务线信息对象
	 * @return VO对象
	 */
	private SourceSystemConfigVO toVO(AmfSourceSystemConfigDO sourceSystemConfigDO,
			BusinessLineConfigVO businessLineConfigVO) {
		if (sourceSystemConfigDO == null) {
			return null;
		}
		SourceSystemConfigVO sourceSystemConfigVO = new SourceSystemConfigVO();
		sourceSystemConfigVO.setId(sourceSystemConfigDO.getId());
		sourceSystemConfigVO.setBusinessLineConfigId(sourceSystemConfigDO.getBusinessLineConfigId());
		sourceSystemConfigVO.setSourceSystemId(sourceSystemConfigDO.getSourceSystemId());
		sourceSystemConfigVO.setSourceSystemName(sourceSystemConfigDO.getSourceSystemName());
		sourceSystemConfigVO.setCreatedBy(sourceSystemConfigDO.getCreatedBy());
		sourceSystemConfigVO.setCreateTime(sourceSystemConfigDO.getCreateTime());
		sourceSystemConfigVO.setUpdatedBy(sourceSystemConfigDO.getUpdatedBy());
		sourceSystemConfigVO.setUpdateTime(sourceSystemConfigDO.getUpdateTime());
		if (businessLineConfigVO != null) {
			sourceSystemConfigVO.setBusinessLineId(businessLineConfigVO.getBusinessLineId());
			sourceSystemConfigVO.setBusinessLineName(businessLineConfigVO.getBusinessLineName());
		}
		return sourceSystemConfigVO;
	}

}
