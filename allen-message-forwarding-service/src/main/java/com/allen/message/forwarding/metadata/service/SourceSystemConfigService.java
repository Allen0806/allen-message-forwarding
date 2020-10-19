package com.allen.message.forwarding.metadata.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.springframework.validation.annotation.Validated;

import com.allen.message.forwarding.metadata.model.SourceSystemConfigVO;
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
	 * @param sourceSystemConfigVO 消息来源系统配置信息
	 */
	@Validated({ ValidationGroup.Insert.class, Default.class })
	void save(@NotNull(message = "消息来源系统配置信息不能为空") @Valid SourceSystemConfigVO sourceSystemConfigVO);

	/**
	 * 修改消息来源系统配置信息
	 * 
	 * @param sourceSystemConfigVO 消息来源系统配置信息
	 */
	@Validated(ValidationGroup.Update.class)
	void update(@NotNull(message = "消息来源系统配置信息不能为空", groups = {
			ValidationGroup.Update.class }) @Valid SourceSystemConfigVO sourceSystemConfigVO);

	/**
	 * 根据主键ID删除消息来源系统配置信息，逻辑删除。如果有对应的消息配置，则不允许删除
	 * 
	 * @param id        主键ID
	 * @param updatedBy 修改人ID
	 */
	void remove(@NotNull(message = "消息来源系统配置信息主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy);

	/**
	 * 根据主键ID获取未删除的消息来源系统配置信息
	 * 
	 * @param id 主键ID
	 * @return 消息来源系统配置信息
	 */
	SourceSystemConfigVO get(@NotNull(message = "消息来源系统配置信息主键ID不能为空") Long id);

	/**
	 * 根据业务线主键统计未删除的消息来源系统配置信息数量
	 * 
	 * @param businessLineConfigId 所属业务线主键
	 * @return 消息来源系统配置信息数量
	 */
	int count(@NotNull(message = "消息来源系统配置信息业务线主键不能为空") Long businessLineConfigId);

	/**
	 * 根据业务线ID分页查询未删除的消息来源系统配置信息
	 * 
	 * @param businessLineConfigId 业务线主键
	 * @param pageNo               当前页数
	 * @param pageSize             每页行数
	 * @return 分页查询结果
	 */
	List<SourceSystemConfigVO> list4Paging(@NotNull(message = "消息来源系统配置信息业务线主键不能为空") Long businessLineConfigId,
			@NotNull(message = "当前页数不能为空") int pageNo, @NotNull(message = "每页行数不能为空") int pageSize);
}
