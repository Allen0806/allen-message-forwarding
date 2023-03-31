package com.allen.message.forwarding.metadata.controller;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allen.message.forwarding.metadata.model.MessageForwardingConfigVO;
import com.allen.message.forwarding.metadata.service.MessageForwardingConfigService;
import com.allen.tool.result.BaseResult;
import com.allen.tool.validation.ValidationGroup;

/**
 * 消息转发配置Controller
 *
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
@RestController
@RequestMapping(path = "/mf/meta/mfc")
public class MessageForwardingConfigController {

	/**
	 * 日志纪录器
	 */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardingConfigController.class);

	/**
	 * 消息转发配置管理服务实例
	 */
	@Autowired
	private MessageForwardingConfigService messageForwardingConfigService;

	/**
	 * 新增消息转发配置信息
	 * 
	 * @param messageConfigDO 消息转发配置信息
	 * @return 新增结果
	 */
	@PostMapping("/save")
	public BaseResult<Object> save(@NotNull(message = "消息转发配置信息不能为空") @Validated({ ValidationGroup.Insert.class,
			Default.class }) @RequestBody MessageForwardingConfigVO messageForwardingConfigVO) {
		messageForwardingConfigService.save(messageForwardingConfigVO);
		return BaseResult.success();
	}

	/**
	 * 修改消息转发配置信息
	 * 
	 * @param messageConfigDO 消息转发配置信息
	 * @return 修改结果
	 */
	@PostMapping("/update")
	public BaseResult<Object> update(@NotNull(message = "消息转发配置信息不能为空") @Validated({ ValidationGroup.Update.class,
			Default.class }) @RequestBody MessageForwardingConfigVO messageForwardingConfigVO) {
		messageForwardingConfigService.update(messageForwardingConfigVO);
		return BaseResult.success();
	}

	/**
	 * 根据主键删除消息转发配置信息，逻辑删除
	 * 
	 * @param id        主键ID
	 * @param updatedBy 修改人ID
	 * @return 删除结果
	 */
	@PostMapping("/remove/{id}/{updatedBy}")
	public BaseResult<Object> remove(@NotNull(message = "消息ID不能为空") @PathVariable("id") Long id,
			@NotNull(message = "修改人ID不能为空") @PathVariable("updatedBy") String updatedBy) {
		messageForwardingConfigService.remove(id, updatedBy);
		return BaseResult.success();
	}

	/**
	 * 根据主键获取消息转发配置信息
	 * 
	 * @param id 主键ID
	 * @return 消息转发配置信息
	 */
	@PostMapping("/get/{id}")
	public BaseResult<MessageForwardingConfigVO> get(
			@NotNull(message = "消息转发配置信息主键ID不能为空") @PathVariable("id") Long id) {
		MessageForwardingConfigVO messageForwardingConfigVO = messageForwardingConfigService.get(id);
		return BaseResult.success(messageForwardingConfigVO);
	}

	/**
	 * 根据消息ID获取消息转发配置信息
	 * 
	 * @param messageId 消息ID
	 * @return 消息转发配置信息
	 */
	@PostMapping("/list/{messageId}")
	public BaseResult<List<MessageForwardingConfigVO>> list(
			@NotNull(message = "消息ID不能为空") @PathVariable("messageId") Integer messageId) {
		List<MessageForwardingConfigVO> MessageForwardingConfigVOList = messageForwardingConfigService.list(messageId);
		return BaseResult.success(MessageForwardingConfigVOList);
	}
}
