package com.allen.message.forwarding.metadata.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.springframework.validation.annotation.Validated;

import com.allen.message.forwarding.metadata.model.MessageForwardingConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageForwardingConfigVO;
import com.allen.tool.validation.ValidationGroup;

public interface MessageForwardingConfigService {

	/**
	 * 新增消息转发配置信息
	 * 
	 * @param messageConfigDO 消息转发配置信息
	 * @return 新增成功的数量
	 */
	@Validated({ ValidationGroup.Insert.class, Default.class })
	void save(@NotNull(message = "消息转发配置信息不能为空") @Valid MessageForwardingConfigVO messageForwardingConfigVO);

	/**
	 * 修改消息转发配置信息
	 * 
	 * @param messageConfigDO 消息转发配置信息
	 * @return 修改成功的数量
	 */
	@Validated({ ValidationGroup.Update.class, Default.class })
	void update(@NotNull(message = "消息转发配置信息不能为空") @Valid MessageForwardingConfigVO messageForwardingConfigVO);

	/**
	 * 根据主键获取消息转发配置信息
	 * 
	 * @param id 主键ID
	 * @return 消息转发配置信息
	 */
	MessageForwardingConfigVO get(Long id);

	/**
	 * 根据主键删除消息转发配置信息，逻辑删除
	 * 
	 * @param id        主键ID
	 * @param updatedBy 修改人ID
	 */
	void remove(@NotNull(message = "主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy);

	/**
	 * 根据消息ID获取消息转发配置数量
	 * 
	 * @param messageId 消息ID
	 * @return 消息转发配置数量
	 */
	int count(@NotNull(message = "消息ID不能为空") Integer messageId);

	/**
	 * 根据消息ID获取消息转发配置信息
	 * 
	 * @param messageId 消息ID
	 * @return 消息转发配置信息
	 */
	List<MessageForwardingConfigVO> list(@NotNull(message = "消息ID不能为空") Integer messageId);

	/**
	 * 根据消息ID获取消息转发配置信息
	 * 
	 * @param messageId 消息ID
	 * @return 消息转发配置信息
	 */
	List<MessageForwardingConfigDTO> list4Process(@NotNull(message = "消息ID不能为空") Integer messageId);
}
