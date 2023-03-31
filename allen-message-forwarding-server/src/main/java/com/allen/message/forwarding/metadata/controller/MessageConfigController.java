package com.allen.message.forwarding.metadata.controller;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allen.message.forwarding.metadata.model.MessageConfigVO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.tool.result.BaseResult;
import com.allen.tool.validation.ValidationGroup;

/**
 * 消息配置管理Controller
 *
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
@RestController
@RequestMapping(path = "/mf/meta/mc")
public class MessageConfigController {

	/**
	 * 日志纪录器
	 */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageConfigController.class);

	/**
	 * 消息配置管理服务实例
	 */
	@Autowired
	@Qualifier("messageConfigServiceImpl")
	private MessageConfigService messageConfigService;

	/**
	 * 新增消息配置信息
	 * 
	 * @param messageConfigVO 消息配置信息
	 * @return 新增结果
	 */
	@PostMapping("/save")
	public BaseResult<Object> save(@NotNull(message = "消息配置信息不能为空") @Validated({ ValidationGroup.Insert.class,
			Default.class }) @RequestBody MessageConfigVO messageConfigVO) {
		messageConfigService.save(messageConfigVO);
		return BaseResult.success();
	}

	/**
	 * 修改消息配置信息
	 * 
	 * @param messageConfigVO 消息配置信息
	 * @return 修改结果
	 */
	@PostMapping("/update")
	public BaseResult<Object> update(@NotNull(message = "消息配置信息不能为空") @Validated({ ValidationGroup.Update.class,
			Default.class }) @RequestBody MessageConfigVO messageConfigVO) {
		messageConfigService.update(messageConfigVO);
		return BaseResult.success();
	}

	/**
	 * 根据消息ID删除消息配置信息，逻辑删除。如果有对应的消息转发配置信息，则不允许删除
	 * 
	 * @param messageId 消息ID
	 * @param updatedBy 修改人ID
	 * @return 删除结果
	 */
	@PostMapping("/remove/{messageId}/{updatedBy}")
	public BaseResult<Object> remove(@NotNull(message = "消息ID不能为空") @PathVariable("messageId") Integer messageId,
			@NotNull(message = "修改人ID不能为空") @PathVariable("updatedBy") String updatedBy) {
		messageConfigService.remove(messageId, updatedBy);
		return BaseResult.success();
	}

	/**
	 * 根据主键ID获取未删除的消息配置信息
	 * 
	 * @param id 主键ID
	 * @return 消息配置信息
	 */
	@PostMapping("/get/{id}")
	public BaseResult<MessageConfigVO> get(@NotNull(message = "消息配置信息主键ID不能为空") @PathVariable("id") Long id) {
		MessageConfigVO messageConfigVO = messageConfigService.get(id);
		return BaseResult.success(messageConfigVO);
	}

	/**
	 * 统计给定的来源系统下的未删除的消息配置数量
	 * 
	 * @param sourceSystemId 来源系统ID
	 * @return 消息配置数量
	 */
	@PostMapping("/count_by_source_system_id/{sourceSystemId}")
	public BaseResult<Integer> countBySourceSystemId(
			@NotNull(message = "来源系统ID不能为空") @PathVariable("sourceSystemId") Integer sourceSystemId) {
		Integer count = messageConfigService.countBySourceSystemId(sourceSystemId);
		return BaseResult.success(count);
	}

	/**
	 * 分页查询未标记为删除的消息配置信息
	 * 
	 * @param sourceSystemId 消息来源系统ID
	 * @param startNo        起始行号
	 * @param pageSize       每页行数
	 * @return 分页查询结果
	 */
	@PostMapping("/list_for_paging/{sourceSystemId}/{pageNo}/{pageSize}")
	public BaseResult<List<MessageConfigVO>> listBySourceSystemId4Paging(
			@NotNull(message = "来源系统ID不能为空") @PathVariable("sourceSystemId") Integer sourceSystemId,
			@NotNull(message = "当前页数不能为空") @PathVariable("pageNo") Integer pageNo,
			@NotNull(message = "每页行数不能为空") @PathVariable("pageSize") Integer pageSize) {
		List<MessageConfigVO> messageConfigVOList = messageConfigService.listBySourceSystemId4Paging(sourceSystemId,
				pageNo, pageSize);
		return BaseResult.success(messageConfigVOList);
	}
}
