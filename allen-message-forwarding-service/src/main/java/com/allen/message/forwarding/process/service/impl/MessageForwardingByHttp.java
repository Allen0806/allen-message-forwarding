package com.allen.message.forwarding.process.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.http.HttpClientUtil;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.result.BaseResult;
import com.allen.tool.string.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 通过HTTP转发消息实现类
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Service("messageForwardingByHttp")
public class MessageForwardingByHttp implements MessageForwarding {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageForwardingByHttp.class);

	/**
	 * 消息管理服务
	 */
	@Autowired
	private MessageManagementService messageManagementService;

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	@Override
	public void forward(MessageForwardingDTO messageForwardingDTO) {
		MessageDTO messageDTO = messageManagementService.getMessage(messageForwardingDTO.getMessageNo());
		String messageContent = messageDTO.getMessageContent();
		Map<String, String> httpHeaders = messageDTO.getHttpHeaders();
		String targetAddress = messageForwardingDTO.getTargetAddress();
		boolean forwardingResult = false;
		try {
			String resultStr = HttpClientUtil.doPost4Json(targetAddress, httpHeaders, messageContent, 3);
			if (StringUtil.isNotBlank(resultStr)) {
				BaseResult<Object> baseResult = JsonUtil.json2Object(resultStr,
						new TypeReference<BaseResult<Object>>() {
						});
				forwardingResult = baseResult.isSuccessful();
			}
		} catch (Exception e) {
			LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo(), e);
			forwardingResult = false;
		}
		messageProcessService.updateForwardingResult(messageForwardingDTO, forwardingResult, Boolean.TRUE);
	}
}
