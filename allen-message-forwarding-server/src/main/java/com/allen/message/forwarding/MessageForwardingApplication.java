package com.allen.message.forwarding;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 消息转发系统启动类
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.allen"})
@EnableScheduling // 开启Spring定时任务支持
@ComponentScan(basePackages = "com.allen")
@MapperScan(basePackages = "com.allen.**.dao")
public class MessageForwardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageForwardingApplication.class, args);

    }

}
