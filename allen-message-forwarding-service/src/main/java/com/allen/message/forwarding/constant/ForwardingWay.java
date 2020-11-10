package com.allen.message.forwarding.constant;

import com.allen.tool.string.StringUtil;

/**
 * 转发方式枚举类
 * 
 * @author Allen
 * @date 2020年11月10日
 * @since 1.0.0
 */
public enum ForwardingWay {
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
	private ForwardingWay(String value) {
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
	public static ForwardingWay instanceOf(String value) {
		if (StringUtil.isBlank(value)) {
			return null;
		}
		if ("01".equals(value)) {
			return HTTP;
		}
		if ("02".equals(value)) {
			return KAFKA;
		}
		if ("03".equals(value)) {
			return ROCKETMQ;
		}
		return null;
	}
}
