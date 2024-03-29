package com.allen.message.forwarding.constant;

import java.util.Objects;

/**
 * 转发状态枚举类
 *
 * @author Allen
 * @date 2020年11月10日
 * @since 1.0.0
 */
public enum ForwardingStatus {
    PROCESSING(0), RETRYING(1), FINISH(2);

    /**
     * 枚举转换值
     */
    private Integer value;

    /**
     * 私有构造方法
     *
     * @param value
     */
    private ForwardingStatus(Integer value) {
        this.value = value;
    }

    /**
     * 获取枚举对应的转换值
     *
     * @return 枚举转换值
     */
    public Integer value() {
        return value;
    }

    /**
     * 根据枚举转换值获取对应的枚举对象
     *
     * @param value 枚举转换值
     * @return 枚举对象
     */
    public static ForwardingStatus instanceOf(Integer value) {
        for (ForwardingStatus item : ForwardingStatus.values()) {
            if (item.value().equals(value)) {
                return item;
            }
        }
        return null;
    }
}
