package com.allen.message.forwarding.metadata.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.springframework.validation.annotation.Validated;

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
	void save(@NotNull(message = "消息所属业务线配置信息不能为空") @Valid MessageConfigVO messageConfigVO);

	/**
	 * 修改消息所属业务线配置信息
	 * 
	 * @param businessLineConfigDTO 消息所属业务线配置信息
	 * @return 修改成功数量
	 */
	@Validated(ValidationGroup.Update.class)
	void update(@NotNull(message = "消息所属业务线配置信息不能为空", groups = {
			ValidationGroup.Update.class }) @Valid MessageConfigVO messageConfigVO);

	/**
	 * 根据主键ID删除消息所属业务线配置信息，逻辑删除。如果有对应的消息来源系统配置信息，则不允许删除
	 * 
	 * @param id        主键ID
	 * @param updatedBy 修改人ID
	 */
	void remove(@NotNull(message = "消息所属业务线配置信息主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy);

	/**
	 * 根据主键ID获取未标记为删除的所属业务线配置信息
	 * 
	 * @param id 主键ID
	 * @return 所属业务线配置信息
	 */
	MessageConfigVO get(@NotNull(message = "消息所属业务线配置信息主键ID不能为空") Long id);

	/**
	 * 根据业务线ID获取未标记为删除的所属业务线配置信息
	 * 
	 * @param businessLineId 业务线ID
	 * @return 所属业务线配置信息
	 */
	MessageConfigVO getByBusinessLineId(@NotNull(message = "消息所属业务线配置信息业务线ID不能为空") String businessLineId);


	/**
	 * 获取未标记为删除的所属业务线配置信息数量
	 * 
	 * @return 所属业务线配置信息数量
	 */
	int count();

	/**
	 * 分页查询未标记为删除的所属业务线配置信息
	 * 
	 * @param startNo  起始行号
	 * @param pageSize 每页行数
	 * @return 分页查询结果
	 */
	List<MessageConfigVO> list4Paging(@NotNull(message = "当前页数不能为空") int pageNo,
			@NotNull(message = "每页行数不能为空") int pageSize);
}
