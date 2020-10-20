package com.allen.message.forwarding.metadata.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.springframework.validation.annotation.Validated;

import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageConfigVO;
import com.allen.tool.validation.ValidationGroup;

/**
 * 消息配置管理服务层接口
 *
 * @author Allen
 * @date 2020年10月16日
 * @since 1.0.0
 */
@Validated
public interface MessageConfigService {

	/**
	 * 新增消息配置信息
	 * 
	 * @param messageConfigVO 消息配置信息
	 */
	@Validated({ ValidationGroup.Insert.class, Default.class })
	void save(@NotNull(message = "消息配置信息不能为空") @Valid MessageConfigVO messageConfigVO);

	/**
	 * 修改消息配置信息
	 * 
	 * @param messageConfigVO 消息配置信息
	 */
	@Validated({ ValidationGroup.Update.class, Default.class })
	void update(@NotNull(message = "消息配置信息不能为空", groups = {
			ValidationGroup.Update.class }) @Valid MessageConfigVO messageConfigVO);

	/**
	 * 根据业务线ID更新消息配置信息的业务线名称
	 * 
	 * @param businessLineId   业务线ID
	 * @param businessLineName 业务线名称
	 */
	void updateBusinessLineName(@NotNull(message = "业务线ID不能为空") String businessLineId,
			@NotNull(message = "业务线名称不能为空") String businessLineName);

	/**
	 * 根据来源系统ID更新来源系统名称
	 * 
	 * @param sourceSystemId   来源系统ID
	 * @param sourceSystemName 来源系统名称
	 */
	void updateSourceSystemName(@NotNull(message = "来源系统ID不能为空") Integer sourceSystemId,
			@NotNull(message = "来源系统名称不能为空") String sourceSystemName);

	/**
	 * 根据主键ID删除消息配置信息，逻辑删除。如果有对应的消息转发配置信息，则不允许删除
	 * 
	 * @param id        主键ID
	 * @param updatedBy 修改人ID
	 */
	void remove(@NotNull(message = "消息配置信息主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy);

	/**
	 * 根据主键ID获取未删除的消息配置信息
	 * 
	 * @param id 主键ID
	 * @return 消息配置信息
	 */
	MessageConfigVO get(@NotNull(message = "消息配置信息主键ID不能为空") Long id);

	/**
	 * 统计给定的来源系统下的未删除的消息配置数量
	 * 
	 * @param sourceSystemId 来源系统ID
	 * @return 消息配置数量
	 */
	int count(@NotNull(message = "来源系统ID不能为空") Integer sourceSystemId);

	/**
	 * 分页查询未标记为删除的消息配置信息
	 * 
	 * @param startNo  起始行号
	 * @param pageSize 每页行数
	 * @return 分页查询结果
	 */
	List<MessageConfigVO> list4Paging(@NotNull(message = "来源系统ID不能为空") Integer sourceSystemId,
			@NotNull(message = "当前页数不能为空") int pageNo, @NotNull(message = "每页行数不能为空") int pageSize);

	/**
	 * 根据消息配置ID获取未删除的消息配置信息，包含消息转发信息
	 * 
	 * @param messageId 消息配置ID
	 * @return 消息配置信息，包含消息转发信息
	 */
	MessageConfigDTO getByMessageId(@NotNull(message = "消息配置ID不能为空") Integer messageId);
}
