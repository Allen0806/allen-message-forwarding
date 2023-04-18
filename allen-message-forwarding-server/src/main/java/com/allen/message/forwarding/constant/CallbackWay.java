package com.allen.message.forwarding.constant;

import com.allen.tool.string.StringUtil;

/**
 * 回调方式枚举类
 *
 * @author Allen
 * @date 2020年11月10日
 * @since 1.0.0
 */
public enum CallbackWay {
    HTTP("01"), KAFKA("02"), ROCKETMQ("03");

    /**
     * 枚举转换值
     */
    private String value;

    /**
     * 私有构造方法
     *
     * @param value
     */
    private CallbackWay(String value) {
        this.value = value;
    }

    /**
     * 获取枚举对应的转换值
     *
     * @return 枚举转换值
     */
    public String value() {
        return value;
    }

    /**
     * 根据枚举转换值获取对应的枚举对象
     *
     * @param value 枚举转换值
     * @return 枚举对象
     */
    public static CallbackWay instanceOf(String value) {
        for (CallbackWay item : CallbackWay.values()) {
            if (item.value().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
