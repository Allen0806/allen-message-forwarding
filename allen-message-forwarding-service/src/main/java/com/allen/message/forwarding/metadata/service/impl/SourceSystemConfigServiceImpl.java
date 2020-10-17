package com.allen.message.forwarding.metadata.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allen.message.forwarding.metadata.dao.SourceSystemConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.result.StatusCode;
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
	 * 消息来源系统配置信息DAO层接口实例
	 */
	@Autowired
	private SourceSystemConfigDAO sourceSystemConfigDAO;

	/**
	 * ID生成器
	 */

	@Transactional
	@Override
	public void save(AmfSourceSystemConfigDO sourceSystemConfigDO) {
		sourceSystemConfigDO.setDeleted(0);
		sourceSystemConfigDO.setCreateTime(LocalDateTime.now());
		sourceSystemConfigDO.setUpdateTime(LocalDateTime.now());
		sourceSystemConfigDAO.save(sourceSystemConfigDO);
		LOGGER.info("保存消息来源系统配置信息成功，业务线名称：{}，来源系统名称：{}", sourceSystemConfigDO.getBusinessLineName(),
				sourceSystemConfigDO.getSourceSystemName());
	}

	@Transactional
	@Override
	public void update(AmfSourceSystemConfigDO sourceSystemConfigDO) {
		String businessLineName = sourceSystemConfigDO.getBusinessLineName();
		String sourceSystemName = sourceSystemConfigDO.getSourceSystemName();
		if (StringUtil.isBlank(businessLineName) && StringUtil.isBlank(sourceSystemName)) {
			throw new CustomBusinessException(StatusCode.PARAM_ERROR.getCode(), "更新消息来源系统信息时业务线名称与来源系统名称不能同时为空");
		}
		// 只允许修改业务线名称及来源系统名称
		AmfSourceSystemConfigDO sourceSystemConfigDONew = new AmfSourceSystemConfigDO();
		sourceSystemConfigDONew.setId(sourceSystemConfigDO.getId());
		if (StringUtil.isNotBlank(businessLineName)) {
			sourceSystemConfigDONew.setBusinessLineName(businessLineName);
		}
		if (StringUtil.isNotBlank(sourceSystemName)) {
			sourceSystemConfigDONew.setSourceSystemName(sourceSystemName);
		}
		sourceSystemConfigDONew.setUpdatedBy(sourceSystemConfigDO.getUpdatedBy());
		sourceSystemConfigDONew.setUpdateTime(LocalDateTime.now());
		sourceSystemConfigDAO.update(sourceSystemConfigDONew);
		// TODO 更新消息信息的业务线名称及来源系统名称

		LOGGER.info("更新消息来源系统配置信息成功，业务线名称：{}，来源系统名称：{}", sourceSystemConfigDO.getBusinessLineName(),
				sourceSystemConfigDO.getSourceSystemName());
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
	public AmfSourceSystemConfigDO get(Long id) {
		return sourceSystemConfigDAO.get(id);
	}

	@Override
	public int count(Long businessLineConfigId) {
		return sourceSystemConfigDAO.count(businessLineConfigId);
	}

	@Override
	public List<AmfSourceSystemConfigDO> listByBusinessLineId4Paging(Long businessLineConfigId, int pageNo,
			int pageSize) {
		int startNo = (pageNo - 1) * pageSize;
		return sourceSystemConfigDAO.list4Paging(businessLineConfigId, startNo, pageSize);
	}

}
