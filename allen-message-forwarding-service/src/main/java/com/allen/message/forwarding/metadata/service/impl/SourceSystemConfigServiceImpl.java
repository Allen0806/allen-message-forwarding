package com.allen.message.forwarding.metadata.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.allen.message.forwarding.metadata.dao.SourceSystemConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.message.forwarding.tool.IdGenerator;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.result.StatusCode;
import com.allen.tool.string.StringUtil;
import com.allen.tool.validation.ValidationGroup;

/**
 * 消息来源系统配置信息管理Service层接口实现类
 *
 * @author Allen
 * @date Jul 20, 2020
 * @since 1.0.0
 */
@Service
@Validated
public class SourceSystemConfigServiceImpl implements SourceSystemConfigService {

	/**
	 * 消息来源系统配置信息DAO层接口实例
	 */
	@Autowired
	private SourceSystemConfigDAO sourceSystemConfigDAO;

	/**
	 * ID生成器
	 */
	@Autowired
	private IdGenerator idGenerator;

	@Validated(ValidationGroup.Insert.class)
	@Transactional
	@Override
	public void save(@Valid AmfSourceSystemConfigDO sourceSystemConfigDO) {
		sourceSystemConfigDO.setSourceSystemId(idGenerator.generateSourceSystemId());
		sourceSystemConfigDO.setDeleted(0);
		sourceSystemConfigDO.setCreateTime(LocalDateTime.now());
		sourceSystemConfigDO.setUpdateTime(LocalDateTime.now());
		sourceSystemConfigDAO.save(sourceSystemConfigDO);
	}

	@Validated(ValidationGroup.Update.class)
	@Transactional
	@Override
	public void update(@Valid AmfSourceSystemConfigDO sourceSystemConfigDO) {
		String businessLineName = sourceSystemConfigDO.getBusinessLineName();
		String sourceSystemName = sourceSystemConfigDO.getSourceSystemName();
		if (StringUtil.isBlank(businessLineName) && StringUtil.isBlank(sourceSystemName)) {
			throw new CustomBusinessException(StatusCode.PARAM_ERROR.getCode(), "更新消息来源系统信息时业务线名称与来源系统名称不能同时为空");
		}
		AmfSourceSystemConfigDO sourceSystemConfigDONew = new AmfSourceSystemConfigDO();
		sourceSystemConfigDONew.setId(sourceSystemConfigDO.getId());
		sourceSystemConfigDONew.setBusinessLineName(businessLineName);
		sourceSystemConfigDONew.setSourceSystemName(sourceSystemName);
		sourceSystemConfigDONew.setUpdatedBy(sourceSystemConfigDO.getUpdatedBy());
		sourceSystemConfigDONew.setUpdateTime(LocalDateTime.now());
		sourceSystemConfigDAO.update(sourceSystemConfigDONew);
		// TODO 更新消息信息的业务线名称及来源系统名称

	}

	@Transactional
	@Override
	public void remove(@NotNull(message = "主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy) {
		// TODO 查询该消息来源系统是否有关联的有效的消息配置信息，如果有则不允许更新
		AmfSourceSystemConfigDO sourceSystemConfigDO = new AmfSourceSystemConfigDO();
		sourceSystemConfigDO.setId(id);
		sourceSystemConfigDO.setDeleted(1);
		sourceSystemConfigDO.setUpdatedBy(updatedBy);
		sourceSystemConfigDO.setUpdateTime(LocalDateTime.now());
		sourceSystemConfigDAO.update(sourceSystemConfigDO);

	}

	@Override
	public AmfSourceSystemConfigDO get(@NotNull(message = "主键ID不能为空") Long id) {
		return sourceSystemConfigDAO.get(id);
	}

	@Override
	public int count(@NotNull(message = "业务线ID不能为空") String businessLineId) {
		return sourceSystemConfigDAO.count(businessLineId);
	}

	@Override
	public List<AmfSourceSystemConfigDO> listByBusinessLineId4Paging(
			@NotNull(message = "业务线ID不能为空") String businessLineId, @NotNull(message = "当前页数不能为空") int pageNo,
			@NotNull(message = "每页行数不能为空") int pageSize) {
		int startNo = pageNo * pageSize;
		return sourceSystemConfigDAO.listByBusinessLineId4Paging(businessLineId, startNo, pageSize);
	}

}
