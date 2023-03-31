package com.allen.message.forwarding.constant;

/**
 * 缓存名称常量类
 *
 * @author Allen
 * @date 2020年10月20日
 * @since 1.0.0
 */
public class MessageConstant {

    /**
     * 消息配置信息缓存命名前缀
     */
    public static final String MESSAGE_CONFIG_CACHE_NAME = "AMF_MESSAGE_CONFIG_CACHE";

    /**
     * 消息配置信息锁命名前缀
     */
    public static final String MESSAGE_CONFIG_LOCK_NAME = "AMF_MESSAGE_CONFIG_LOCK";

    /**
     * 消息转发明细信息锁命名前缀
     */
    public static final String MESSAGE_FORWARDING_LOCK_NAME = "AMF_MESSAGE_FORWARDING_LOCK";

    /**
     * 消息转发结果回调锁命名前缀
     */
    public static final String MESSAGE_CALLBACK_LOCK_NAME = "AMF_MESSAGE_CALLBACK_LOCK";

    /**
     * 消息处理线程池名称
     */
    public static final String MESSAGE_FORWARDING_THREAD_POOL_NAME = "message-forwarding";

    /**
     * 是对应的字符串
     */
    public static final Integer YES = 1;

    /**
     * 否对应的字符串
     */
    public static final Integer NO = 0;

    /**
     * 默认最大回调重试次数，固定为3
     */
    public static final Integer MAX_CALLBACK_RETRY_TIMES = 3;
}
