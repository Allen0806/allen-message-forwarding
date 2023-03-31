package com.allen.message.forwarding.process.service.impl;

import com.allen.message.forwarding.constant.CallbackWay;
import com.allen.message.forwarding.process.service.MessageCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * 回调服务工厂
 *
 * @author Allen
 * @date 2020年12月1日
 * @since 1.0.0
 */
@Component
public class MessageCallbackFactory {

    /**
     * 静态服务实例集合
     */
    private static Map<String, MessageCallback> services;

    /**
     * 注入的服务实例
     */
    @Autowired
    private Map<String, MessageCallback> serviceMap;

    @PostConstruct
    public void init() {
        services = serviceMap;
    }

    /**
     * 根据回调方式获取对应的回调服务，增加新的回调方式时扩展此方法
     *
     * @param callbackWay 回调方式
     * @return 回调服务
     */
    public static MessageCallback getService(CallbackWay callbackWay) {

        switch (callbackWay) {
            case HTTP:
                return services.get("messageCallbackByHttp");
            case ROCKETMQ:
                return services.get("messageCallbackByRocketMQ");
            case KAFKA:
                return services.get("messageCallbackByKafka");
            default:
                return null;
        }
    }
}
