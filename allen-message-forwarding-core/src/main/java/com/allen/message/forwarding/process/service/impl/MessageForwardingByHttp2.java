package com.allen.message.forwarding.process.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.allen.message.forwarding.process.model.MessageDTO;
import com.allen.message.forwarding.process.model.MessageForwardingDTO;
import com.allen.message.forwarding.process.service.MessageForwarding;
import com.allen.message.forwarding.process.service.MessageManagementService;
import com.allen.message.forwarding.process.service.MessageProcessService;
import com.allen.tool.json.JsonUtil;
import com.allen.tool.result.BaseResult;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 通过HTTP转发消息实现类，基于java 11 自带的httpclient实现
 * 
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Service("messageForwardingByHttp2")
public class MessageForwardingByHttp2 implements MessageForwarding {

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

	/**
	 * 初始化httpclient
	 */
	private final HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL)
			.connectTimeout(Duration.ofMillis(5000)).build();

	@Override
	public void forward(MessageForwardingDTO messageForwardingDTO) {
		MessageDTO messageDTO = messageManagementService.getMessage(messageForwardingDTO.getMessageNo());
		String messageContent = messageDTO.getMessageContent();
		Map<String, String> httpHeaders = messageDTO.getHttpHeaders();
		String targetAddress = messageForwardingDTO.getTargetAddress();
		boolean forwardingResult = false;

		HttpRequest request = HttpRequest.newBuilder(URI.create(targetAddress))
				.header("Content-Type", "application/json")
				.headers(httpHeaders.entrySet().stream().flatMap(e -> Stream.of(e.getKey(), e.getValue()))
						.collect(Collectors.toList()).stream().toArray(String[]::new))
				.timeout(Duration.ofMillis(5000)).POST(HttpRequest.BodyPublishers.ofString(messageContent)).build();
//		try {
//			// 同步请求处理
//			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//			forwardingResult = forwardResult(response);
//			if (!forwardingResult) {
//				LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo());
//			}
//		} catch (Exception e) {
//			LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo(), e);
//			forwardingResult = false;
//		}

		try {
			// 异步请求处理
			CompletableFuture<Boolean> result = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
					.thenApply(e -> forwardResult(e));
			forwardingResult = result.get(5000, TimeUnit.MILLISECONDS);
			if (!forwardingResult) {
				LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo());
			}
		} catch (Exception e) {
			LOGGER.error("通过http接口转发消息异常，消息流水号：" + messageDTO.getMessageNo(), e);
			forwardingResult = false;
		}
		messageProcessService.updateForwardingResult(messageForwardingDTO, forwardingResult, Boolean.TRUE);
	}

	/**
	 * 处理返回结果
	 * 
	 * @param response
	 * @return
	 */
	private boolean forwardResult(HttpResponse<String> response) {
		if (response.statusCode() != 200) {
			return false;
		}
		try {
			String body = response.body();
			BaseResult<Object> baseResult = JsonUtil.json2Object(body, new TypeReference<BaseResult<Object>>() {
			});
			return baseResult.isSuccessful();
		} catch (Exception e) {
			LOGGER.error("解析http返回结果失败", e);
			return false;
		}
	}
}
