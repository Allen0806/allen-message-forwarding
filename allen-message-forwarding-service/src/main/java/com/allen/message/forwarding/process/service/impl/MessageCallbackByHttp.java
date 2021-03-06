package com.allen.message.forwarding.process.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.allen.message.forwarding.process.model.ForwardingMessage4Callback;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageCallback;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.result.BaseResult;
import com.allen.tool.string.StringUtil;

/**
 * 通过HTTP方式实现消息回调
 * 
 * @author Allen
 * @date 2020年12月2日
 * @since 1.0.0
 */
@Service("messageCallbackByHttp")
public class MessageCallbackByHttp implements MessageCallback {
	
	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallbackByHttp.class);

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	/**
	 * restTemplate 实例
	 */
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public void callback(MessageForwardingDTO messageForwardingDTO) {
		String callbackUrl = messageForwardingDTO.getCallbackUrl();
		if (StringUtil.isBlank(callbackUrl)) {
			LOGGER.error("回调URL为空，转发明细信息：{}", messageForwardingDTO);
			messageProcessService.updateCallbackResult(messageForwardingDTO, false, false);
			return;
		}
		ForwardingMessage4Callback forwardingMessage4Callback = new ForwardingMessage4Callback();
		forwardingMessage4Callback.setMessageNo(messageForwardingDTO.getMessageNo());
		forwardingMessage4Callback.setMessageKeyword(messageForwardingDTO.getMessageKeyword());
		forwardingMessage4Callback.setMessageId(messageForwardingDTO.getMessageId());
		forwardingMessage4Callback.setForwardingId(messageForwardingDTO.getForwardingId());
		forwardingMessage4Callback.setForwardingResult(messageForwardingDTO.getForwardingResult());
		boolean callbackResult = false;
		try {
			BaseResult<?> baseResult = restTemplate.postForObject(callbackUrl,
					JsonUtil.object2Json(forwardingMessage4Callback), BaseResult.class);
			callbackResult = baseResult.isSuccessful();
		} catch (Exception e) {
			LOGGER.error("消息回调出错，转发明细信息：" + messageForwardingDTO, e);
			callbackResult = false;
		}
		messageProcessService.updateCallbackResult(messageForwardingDTO, callbackResult, Boolean.TRUE);
	}

}
