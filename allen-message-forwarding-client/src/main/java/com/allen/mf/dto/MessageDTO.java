package com.allen.mf.dto;

import java.util.Map;

import io.swagger.annotations.SwaggerDefinition;

/**
 * 消息传入对象
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 *
 */
@SwaggerDefinition
public class MessageDTO implements java.io.Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 3413492938377456162L;

	/**
	 * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
	 */
	private String messageNo;

	/**
	 * 业务线ID，最长20位
	 */
	private String businessLineId;

	/**
	 * 来源系统ID，固定4位
	 */
	private Integer sourceSystemId;

	/**
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	private Integer messageId;

	/**
	 * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
	 */
	private String messageKeyword;

	/**
	 * 通过Http接口转发消息时，设置到http header里的参数，比如接口编号等
	 */
	private Map<String, String> httpHeaders;

	/**
	 * 要转发的消息内容
	 */
	private String messageContent;

	public String getMessageNo() {
		return messageNo;
	}

	public void setMessageNo(String messageNo) {
		this.messageNo = messageNo;
	}

	public String getBusinessLineId() {
		return businessLineId;
	}

	public void setBusinessLineId(String businessLineId) {
		this.businessLineId = businessLineId;
	}

	public Integer getSourceSystemId() {
		return sourceSystemId;
	}

	public void setSourceSystemId(Integer sourceSystemId) {
		this.sourceSystemId = sourceSystemId;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public String getMessageKeyword() {
		return messageKeyword;
	}

	public void setMessageKeyword(String messageKeyword) {
		this.messageKeyword = messageKeyword;
	}

	public Map<String, String> getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("MessageDTO[").append("messageNo=").append(messageNo)
				.append(", businessLineId=").append(businessLineId).append(", sourceSystemId=").append(sourceSystemId)
				.append(", messageId=").append(messageId).append(", messageKeyword=").append(messageKeyword)
				.append(", httpHeaders=").append(httpHeaders).append(", messageContent=").append(messageContent)
				.append("]").toString();
	}

}
