package com.allen.message.forwarding.metadata.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;
import com.allen.tool.validation.ValidationGroup;

/**
 * 消息来源系统配置信息管理Service层接口
 *
 * @author Allen
 * @date Jul 20, 2020
 * @since 1.0.0
 */
@Validated
public interface SourceSystemConfigService {

	/**
	 * 新增消息来源系统配置信息
	 * 
	 * @param sourceSystemConfigDO 消息来源系统配置信息
	 */
	@Validated(ValidationGroup.Insert.class)
	void save(@Valid AmfSourceSystemConfigDO sourceSystemConfigDO);

	/**
	 * 修改消息来源系统配置信息
	 * 
	 * @param sourceSystemConfigDO
	 */
	@Validated(ValidationGroup.Update.class)
	void update(@Valid AmfSourceSystemConfigDO sourceSystemConfigDO);

	/**
	 * 根据主键ID删除消息来源系统配置信息，逻辑删除。如果有对应的消息配置，则不允许删除
	 * 
	 * @param id        主键ID
	 * @param updatedBy 修改人ID
	 */
	void remove(@NotNull(message = "主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy);

	/**
	 * 根据主键ID获取消息来源系统配置信息
	 * 
	 * @param id 主键ID
	 * @return 消息来源系统配置信息
	 */
	AmfSourceSystemConfigDO get(@NotNull(message = "主键ID不能为空") Long id);

	/**
	 * 根据业务线ID统计消息来源系统配置信息数量
	 * 
	 * @param businessLineId 业务线ID
	 * @return 消息来源系统配置信息数量
	 */
	int count(@NotNull(message = "业务线ID不能为空") String businessLineId);

	/**
	 * 根据业务线ID分页查询消息来源系统配置信息
	 * 
	 * @param businessLineId 业务线ID
	 * @param pageNo         当前页数
	 * @param pageSize       每页行数
	 * @return 分页查询结果
	 */
	List<AmfSourceSystemConfigDO> listByBusinessLineId4Paging(@NotNull(message = "业务线ID不能为空") String businessLineId,
			@NotNull(message = "当前页数不能为空") int pageNo, @NotNull(message = "每页行数不能为空") int pageSize);
}
