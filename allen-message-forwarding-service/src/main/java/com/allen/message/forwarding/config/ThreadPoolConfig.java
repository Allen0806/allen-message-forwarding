package com.allen.message.forwarding.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Spring Boot ThreadPoolTaskExecutor 配置类
 * 
 * @author Allen
 * @date 2020年11月19日
 * @since 1.0.0
 */
@Configuration
@EnableAsync // 开启异步方法
public class ThreadPoolConfig {

	/**
	 * 用于回调处理的线程池
	 * 
	 * @return 线程池对象
	 */
	@Bean("callbackExecutor")
	public Executor callbackExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		// 设置线程池参数信息
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(50);
		taskExecutor.setQueueCapacity(200);
		taskExecutor.setKeepAliveSeconds(10);
		taskExecutor.setThreadNamePrefix("message-callback-");
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setAwaitTerminationSeconds(60);
		// 修改拒绝策略为使用当前线程执行
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		// 初始化线程池
		taskExecutor.initialize();
		return taskExecutor;
	}

	/**
	 * 用于转发处理的线程池
	 * 
	 * @return 线程池对象
	 */
	@Bean("forwardingExecutor")
	public Executor forwardingExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		// 设置线程池参数信息
		taskExecutor.setCorePoolSize(10);
		taskExecutor.setMaxPoolSize(50);
		taskExecutor.setQueueCapacity(200);
		taskExecutor.setKeepAliveSeconds(10);
		taskExecutor.setThreadNamePrefix("message-forwarding-");
		taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
		taskExecutor.setAwaitTerminationSeconds(60);
		taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		// 初始化线程池
		taskExecutor.initialize();
		return taskExecutor;
	}
}
