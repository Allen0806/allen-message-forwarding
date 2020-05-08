package com.allen.mf.dto;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消息传入对象
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 *
 */
@ApiModel("消息对象")
public class MessageDTO implements java.io.Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 3413492938377456162L;

	/**
	 * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
	 */
	@ApiModelProperty(name = "消息流水号", value = "固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复", dataType = "String", required = true)
	private String messageNo;

	/**
	 * 业务线ID，最长20位
	 */
	@ApiModelProperty(name = "业务线ID", value = "最长20位", dataType = "String", required = true)
	private String businessLineId;

	/**
	 * 来源系统ID，固定4位
	 */
	@ApiModelProperty(value = "来源系统ID", dataType = "Integer", required = true)
	private Integer sourceSystemId;

	/**
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	@ApiModelProperty(name = "消息ID", value = "消息配置信息里的消息ID，固定6位", dataType = "Integer", required = true)
	private Integer messageId;

	/**
	 * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
	 */
	@ApiModelProperty(name = "消息关键字", value = "方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位", dataType = "String", required = true)
	private String messageKeyword;

	/**
	 * 通过Http接口转发消息时，设置到http header里的参数，比如接口编号等
	 */
	@ApiModelProperty(name = "Http Header 参数", value = "通过Http接口转发消息时，设置到http header里的参数，比如接口编号等", dataType = "Map<String, String>", required = false)
	private Map<String, String> httpHeaders;

	/**
	 * 要转发的消息内容
	 */
	@ApiModelProperty(name = "消息内容", dataType = "String", required = true)
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
