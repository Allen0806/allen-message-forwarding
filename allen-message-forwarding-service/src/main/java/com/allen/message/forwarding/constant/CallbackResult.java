package com.allen.message.forwarding.constant;

import java.util.Objects;

/**
 * 回调结果枚举类
 * 
 * @author Allen
 * @date 2020年11月10日
 * @since 1.0.0
 */
public enum CallbackResult {
	FAILURE(0), SUCCESS(1), PROCESSING(2);

	/**
	 * 枚举转换值
	 */
	private Integer value;

	/**
	 * 私有构造方法
	 * 
	 * @param value
	 */
	private CallbackResult(Integer value) {
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
	public static CallbackResult instanceOf(Integer value) {
		if (Objects.isNull(value)) {
			return null;
		}
		if (value == 0) {
			return FAILURE;
		}
		if (value == 1) {
			return SUCCESS;
		}
		if (value == 2) {
			return PROCESSING;
		}
		return null;
	}
}
