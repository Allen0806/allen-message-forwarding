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
import com.allen.message.forwarding.metadata.model.BusinessLineConfigVO;
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
	 * 业务线信息DAO层接口实例
	 */
	@Autowired
	private BusinessLineConfigDAO businessLineConfigDAO;

	@Transactional
	@Override
	public void save(BusinessLineConfigVO businessLineConfigVO) {
		AmfBusinessLineConfigDO businessLineConfigDO = toDO(businessLineConfigVO);
		businessLineConfigDO.setDeleted(0);
		LocalDateTime now = LocalDateTime.now();
		businessLineConfigDO.setCreateTime(now);
		businessLineConfigDO.setUpdateTime(now);
		if (StringUtil.isBlank(businessLineConfigDO.getUpdatedBy())) {
			businessLineConfigDO.setUpdatedBy(businessLineConfigDO.getCreatedBy());
		}
		businessLineConfigDAO.save(businessLineConfigDO);
		LOGGER.info("保存消息所属业务线配置信息成功，业务线名称：{}，创建人：{}", businessLineConfigVO.getBusinessLineName(),
				businessLineConfigVO.getCreatedBy());
	}

	@Transactional
	@Override
	public void update(BusinessLineConfigVO businessLineConfigVO) {
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(businessLineConfigVO.getId());
		if (businessLineConfigDO == null) {
			LOGGER.info("未查到对应的业务线信息，业务线主键：{}", businessLineConfigVO.getId());
			return;
		}
		if (businessLineConfigDO.getBusinessLineName().equals(businessLineConfigVO.getBusinessLineName())) {
			LOGGER.info("业务线名称没有变化，不进行消息所属业务线配置信息更新操作，业务线名称：{}", businessLineConfigVO.getBusinessLineName());
			return;
		}
		businessLineConfigDO.setBusinessLineName(businessLineConfigVO.getBusinessLineName());
		businessLineConfigDO.setUpdatedBy(businessLineConfigVO.getUpdatedBy());
		businessLineConfigDO.setUpdateTime(LocalDateTime.now());
		businessLineConfigDAO.update(businessLineConfigDO);
		// TODO 更新消息配置信息的业务线名称

		LOGGER.info("更新消息所属业务线配置信息成功，业务线名称：{}，修改人：{}", businessLineConfigVO.getBusinessLineName(),
				businessLineConfigVO.getUpdatedBy());
	}

	@Transactional
	@Override
	public void remove(Long id, String updatedBy) {
		// 判断是否存在关联的未标记为删除的来源系统信息，如果存在，则不允许删除
		int sourceSystemAmount = sourceSystemConfigService.count(id);
		if (sourceSystemAmount > 0) {
			LOGGER.info("存在未删除的来源系统信息，不能进行业务线配置信息删除操作，业务线主键：{}", id);
			throw new CustomBusinessException("MF0001", "存在未删除的来源系统信息，不能进行业务线配置信息删除操作");
		}
		AmfBusinessLineConfigDO businessLineConfigDO = new AmfBusinessLineConfigDO();
		businessLineConfigDO.setId(id);
		businessLineConfigDO.setDeleted(1);
		businessLineConfigDO.setUpdatedBy(updatedBy);
		businessLineConfigDAO.update(businessLineConfigDO);
		LOGGER.info("删除消息所属业务线配置信息成功，业务线主键：{}，删除人{}", id, updatedBy);
	}

	@Override
	public BusinessLineConfigVO get(Long id) {
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(id);
		return toVO(businessLineConfigDO);
	}

	@Override
	public BusinessLineConfigVO getByBusinessLineId(String businessLineId) {
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.getByBusinessLineId(businessLineId);
		return toVO(businessLineConfigDO);
	}

	@Override
	public List<BusinessLineConfigVO> list4Fuzzy(String businessLineId, String businessLineName) {
		// TODO Auto-generated method stub
		if (StringUtil.isBlank(businessLineId) && StringUtil.isBlank(businessLineName)) {
			LOGGER.info("业务线ID和业务线名称不能同时为空");
			throw new CustomBusinessException("MF0002", "业务线ID和业务线名称不能同时为空");
		}
		if (StringUtil.isNotBlank(businessLineId)) {
			businessLineId = businessLineId.trim() + "%";
		}
		if (StringUtil.isNotBlank(businessLineName)) {
			businessLineName = businessLineName.trim() + "%";
		}
		List<AmfBusinessLineConfigDO> businessLineConfigDOList = businessLineConfigDAO.list4Fuzzy(businessLineId,
				businessLineName);
		return toVOList(businessLineConfigDOList);
	}

	@Override
	public int count() {
		return businessLineConfigDAO.count();
	}

	@Override
	public List<BusinessLineConfigVO> list4Paging(int pageNo, int pageSize) {
		if (pageNo < 1 || pageSize < 1) {
			throw new CustomBusinessException("MF0003", "当前页数或每页行数不能小于1");
		}
		int startNo = (pageNo - 1) * pageSize;
		List<AmfBusinessLineConfigDO> businessLineConfigDOList = businessLineConfigDAO.list4Paging(startNo, pageSize);
		return toVOList(businessLineConfigDOList);
	}

	/**
	 * 将VO对象转换为DO对象
	 * 
	 * @param businessLineConfigVO VO对象
	 * @return DO对象
	 */
	private AmfBusinessLineConfigDO toDO(BusinessLineConfigVO businessLineConfigVO) {
		if (businessLineConfigVO == null) {
			return null;
		}
		AmfBusinessLineConfigDO businessLineConfigDO = new AmfBusinessLineConfigDO();
		businessLineConfigDO.setId(businessLineConfigVO.getId());
		businessLineConfigDO.setBusinessLineId(businessLineConfigVO.getBusinessLineId());
		businessLineConfigDO.setBusinessLineName(businessLineConfigVO.getBusinessLineName());
		businessLineConfigDO.setCreatedBy(businessLineConfigVO.getCreatedBy());
		businessLineConfigDO.setCreateTime(businessLineConfigVO.getCreateTime());
		businessLineConfigDO.setUpdatedBy(businessLineConfigVO.getUpdatedBy());
		businessLineConfigDO.setUpdateTime(businessLineConfigVO.getUpdateTime());
		return businessLineConfigDO;
	}

	/**
	 * 将DO对象转换为VO对象
	 * 
	 * @param businessLineConfigDO DO对象
	 * @return VO对象
	 */
	private BusinessLineConfigVO toVO(AmfBusinessLineConfigDO businessLineConfigDO) {
		if (businessLineConfigDO == null) {
			return null;
		}
		BusinessLineConfigVO businessLineConfigVO = new BusinessLineConfigVO();
		businessLineConfigVO.setId(businessLineConfigDO.getId());
		businessLineConfigVO.setBusinessLineId(businessLineConfigDO.getBusinessLineId());
		businessLineConfigVO.setBusinessLineName(businessLineConfigDO.getBusinessLineName());
		businessLineConfigVO.setCreatedBy(businessLineConfigDO.getCreatedBy());
		businessLineConfigVO.setCreateTime(businessLineConfigDO.getCreateTime());
		businessLineConfigVO.setUpdatedBy(businessLineConfigDO.getUpdatedBy());
		businessLineConfigVO.setUpdateTime(businessLineConfigDO.getUpdateTime());
		return businessLineConfigVO;
	}

	/**
	 * 将DO列表转换为VO列表
	 * 
	 * @param businessLineConfigDOList DO列表
	 * @return VO列表
	 */
	private List<BusinessLineConfigVO> toVOList(List<AmfBusinessLineConfigDO> businessLineConfigDOList) {
		if (businessLineConfigDOList == null || businessLineConfigDOList.isEmpty()) {
			return Collections.emptyList();
		}
		return businessLineConfigDOList.stream().map(e -> toVO(e)).collect(Collectors.toList());
	}

}
