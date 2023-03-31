package com.allen.message.forwarding.process.job;

import com.allen.message.forwarding.process.service.MessageProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 消息处理定时任务，基于Spring实现
 *
 * @author Allen
 * @date 2020年11月23日
 * @since 1.0.0
 */
@Component
public class MessageProcessSpringJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessSpringJob.class);

    /**
     * 消息处理服务
     */
    @Autowired
    private MessageProcessService messageProcessService;

    /**
     * 消息转发重试任务，每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void forwardingRetryJobHandler() {
        LOGGER.info("消息转发重试任务开始执行");
        try {
            messageProcessService.retryForward();
        } catch (Exception e) {
            LOGGER.error("消息转发重试任务执行失败", e);
        }
        LOGGER.info("消息转发重试任务执行结束");
    }

    /**
     * 消息回调重试任务，每5分钟执行一次
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void callbackRetryJobHandler() {
        LOGGER.info("消息回调重试任务开始执行");
        try {
            messageProcessService.retryCallback();
        } catch (Exception e) {
            LOGGER.error("消息回调重试任务执行失败", e);
        }
        LOGGER.info("消息回调重试任务执行结束");
    }

    /**
     * 迁移历史消息任务，每天0点执行一次
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void migrationJobHandler() {
        LOGGER.info("迁移历史消息任务开始执行");
        try {
            messageProcessService.migrate();
        } catch (Exception e) {
            LOGGER.error("迁移历史消息任务执行失败", e);
        }
        LOGGER.info("迁移历史消息任务执行结束");
    }
}
