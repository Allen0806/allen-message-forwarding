package com.allen.message.forwarding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 消息转发系统自定义配置信息，不加RefreshScope注解也可以从nacos上获取到最新的配置信息
 *
 * @author allen
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "allen.message.forwarding.message")
public class MessageProperty {

    /**
     * 保留天数
     */
    private Integer retentionDays;
}
