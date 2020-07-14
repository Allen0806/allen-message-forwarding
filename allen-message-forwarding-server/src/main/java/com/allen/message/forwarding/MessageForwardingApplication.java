package com.allen.message.forwarding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 消息转发系统启动类
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 *
 */
@SpringBootApplication
// 仅适用于Eureka注册中心
@EnableEurekaClient
// 适用于其他注册中心
//@EnableDiscoveryClient 
//@EnableFeignClients
@ComponentScan(basePackages = "com.allen")
@MapperScan(basePackages = "com.allen.**.dao")
public class MessageForwardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageForwardingApplication.class, args);

	}

}
