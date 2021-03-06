package com.allen.message.forwarding.process.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.allen.message.forwarding.process.service.MessageProcessService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;

/**
 * 消息处理定时任务
 * 
 * @author Allen
 * @date 2020年11月23日
 * @since 1.0.0
 */
@Component
public class MessageProcessJob {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessJob.class);

	/**
	 * 消息处理服务
	 */
	@Autowired
	private MessageProcessService messageProcessService;

	/**
	 * 消息转发重试任务
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@XxlJob("forwardingRetryJobHandler")
	public ReturnT<String> forwardingRetryJobHandler(String param) throws Exception {
		XxlJobLogger.log("消息转发重试任务开始执行");
		messageProcessService.retryForward();
		return ReturnT.SUCCESS;
	}

	/**
	 * 消息回调重试任务
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@XxlJob("callbackRetryJobHandler")
	public ReturnT<String> callbackRetryJobHandler(String param) throws Exception {
		XxlJobLogger.log("消息回调重试任务开始执行");
		messageProcessService.retryCallback();
		return ReturnT.SUCCESS;
	}
	
	/**
	 * 历史消息迁移任务
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@XxlJob("migrationJobHandler")
	public  ReturnT<String> migrationJobHandler(String param) throws Exception {
		XxlJobLogger.log("历史消息迁移任务开始执行");
		messageProcessService.migrate();
		return ReturnT.SUCCESS;
	}
}
