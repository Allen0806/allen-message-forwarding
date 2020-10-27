package com.allen.message.forwarding.metadata.service.impl;

import static com.allen.message.forwarding.metadata.constant.StatusCodeConstant.*;

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
import com.allen.message.forwarding.metadata.service.MessageConfigService;
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

	/**
	 * 来源系统配置管理服务实例
	 */
	@Autowired
	private SourceSystemConfigService sourceSystemConfigService;

	/**
	 * 消息配置管理服务实例
	 */
	@Autowired
	private MessageConfigService messageConfigService;

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
		// 时间可以不设置
		LocalDateTime now = LocalDateTime.now();
		businessLineConfigDO.setCreateTime(now);
		businessLineConfigDO.setUpdateTime(now);
		if (StringUtil.isBlank(businessLineConfigDO.getUpdatedBy())) {
			businessLineConfigDO.setUpdatedBy(businessLineConfigDO.getCreatedBy());
		}
		int count = businessLineConfigDAO.save(businessLineConfigDO);
		if (count == 0) {
			LOGGER.error("保存业务线信息失败，业务线名称：{}，创建人：{}", businessLineConfigDO.getBusinessLineName(),
					businessLineConfigDO.getCreatedBy());
			throw new CustomBusinessException(MF_0101);
		}
		LOGGER.info("保存业务线信息成功，业务线名称：{}，创建人：{}", businessLineConfigDO.getBusinessLineName(),
				businessLineConfigDO.getCreatedBy());
	}

	@Transactional
	@Override
	public void update(BusinessLineConfigVO businessLineConfigVO) {
		Long id = businessLineConfigVO.getId();
		String newBusinessLineName = businessLineConfigVO.getBusinessLineName();
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(id);
		if (businessLineConfigDO == null) {
			LOGGER.error("不存在对应的业务线信息，业务线主键：{}", id);
			throw new CustomBusinessException(MF_0102);
		}

		if (businessLineConfigDO.getBusinessLineName().equals(newBusinessLineName)) {
			LOGGER.info("业务线名称没有变化，不进行业务线信息更新操作，业务线名称：{}", newBusinessLineName);
			return;
		}
		String updatedBy = businessLineConfigVO.getUpdatedBy();
		String businessLineId = businessLineConfigDO.getBusinessLineId();
		businessLineConfigDO.setBusinessLineName(newBusinessLineName);
		businessLineConfigDO.setUpdatedBy(updatedBy);
		// 通过更新时间实现乐观锁
		int count = businessLineConfigDAO.update(businessLineConfigDO);
		if (count == 0) {
			LOGGER.error("更新业务线信息失败，业务线ID：{}，修改人：{}", businessLineId, updatedBy);
			throw new CustomBusinessException(MF_0103);
		}
		// 更新消息配置信息的业务线名称
		messageConfigService.updateBusinessLineName(businessLineId, newBusinessLineName, updatedBy);
		LOGGER.info("更新业务线信息成功，业务线名称：{}，修改人：{}", newBusinessLineName, updatedBy);
	}

	@Transactional
	@Override
	public void remove(Long id, String updatedBy) {
		AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(id);
		if (businessLineConfigDO == null) {
			LOGGER.error("不存在对应的业务线信息，业务线主键：{}", id);
			throw new CustomBusinessException(MF_0102);
		}
		String businessLineName = businessLineConfigDO.getBusinessLineName();
		// 判断是否存在关联的未标记为删除的来源系统信息，如果存在，则不允许删除
		int sourceSystemAmount = sourceSystemConfigService.count(id);
		if (sourceSystemAmount > 0) {
			LOGGER.error("存在来源系统信息，不能进行业务线信息删除操作，业务线名称：{}", businessLineName);
			throw new CustomBusinessException(MF_0104);
		}
		businessLineConfigDO.setDeleted(1);
		businessLineConfigDO.setUpdatedBy(updatedBy);
		int count = businessLineConfigDAO.update(businessLineConfigDO);
		if (count == 0) {
			LOGGER.error("删除业务线信息失败，业务线名称：{}，删除人：{}", businessLineName, updatedBy);
			throw new CustomBusinessException(MF_0105);
		}
		LOGGER.info("删除消业务线信息成功，业务线名称：{}，删除人{}", businessLineName, updatedBy);
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
		if (StringUtil.isBlank(businessLineId) && StringUtil.isBlank(businessLineName)) {
			throw new CustomBusinessException(MF_0106);
		}
		if (StringUtil.isNotBlank(businessLineId)) {
			if (businessLineId.contains("%")) {
				LOGGER.error("业务线ID中不能包含%，业务线ID：{}", businessLineId);
				throw new CustomBusinessException(MF_0107);
			}
			businessLineId = businessLineId.trim() + "%";
		}
		if (StringUtil.isNotBlank(businessLineName)) {
			if (businessLineName.contains("%")) {
				LOGGER.error("业务线名称中不能包含%，业务线ID：{}", businessLineId);
				throw new CustomBusinessException(MF_0108);
			}
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
			throw new CustomBusinessException(MF_0001);
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
		return businessLineConfigDOList.parallelStream().map(e -> toVO(e)).collect(Collectors.toList());
	}

}
