package com.allen.message.forwarding.process.model;

import java.time.LocalDateTime;

/**
 * 消息DO类
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 *
 */
public class AmfMessageDO implements java.io.Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 4404701789369107774L;

	/**
	 * 主键ID，修改时不可为空
	 */
	private Long id;

	/**
	 * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
	 */
	private String messageNo;

	/**
	 * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
	 */
	private String messageKeyword;

	/**
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	private Integer messageId;

	/**
	 * 业务线ID，最长20位
	 */
	private String businessLineId;

	/**
	 * 来源系统ID，固定4位
	 */
	private Integer sourceSystemId;

	/**
	 * 通过Http接口转发消息时，设置到http header里的参数，比如接口编号等，Map的json格式
	 */
	private String httpHeaders;

	/**
	 * 要转发的消息内容
	 */
	private String messageContent;

	/**
	 * 应转发的总数量，即为消息转发配置的数量
	 */
	private Integer forwardingTotalAmount;

	/**
	 * 实际转发成功数量
	 */
	private Integer forwardingSuccessAmount;

	/**
	 * 创建时间，默认值为系统当前时间，不可修改
	 */
	private LocalDateTime createTime;

	/**
	 * 最后修改时间，默认值为系统当前时间，数据修改时自动更新
	 */
	private LocalDateTime updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageNo() {
		return messageNo;
	}

	public void setMessageNo(String messageNo) {
		this.messageNo = messageNo;
	}

	public String getMessageKeyword() {
		return messageKeyword;
	}

	public void setMessageKeyword(String messageKeyword) {
		this.messageKeyword = messageKeyword;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
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

	public String getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(String httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public Integer getForwardingTotalAmount() {
		return forwardingTotalAmount;
	}

	public void setForwardingTotalAmount(Integer forwardingTotalAmount) {
		this.forwardingTotalAmount = forwardingTotalAmount;
	}

	public Integer getForwardingSuccessAmount() {
		return forwardingSuccessAmount;
	}

	public void setForwardingSuccessAmount(Integer forwardingSuccessAmount) {
		this.forwardingSuccessAmount = forwardingSuccessAmount;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("AmfMessageDO[").append("id=").append(id).append("messageNo=")
				.append(messageNo).append(", messageKeyword=").append(messageKeyword).append(", messageId=")
				.append(messageId).append(", businessLineId=").append(businessLineId).append(", sourceSystemId=")
				.append(sourceSystemId).append(", httpHeaders=").append(httpHeaders).append(", messageContent=")
				.append(messageContent).append(", forwardingTotalAmount=").append(forwardingTotalAmount)
				.append(", forwardingSuccessAmount=").append(forwardingSuccessAmount).append(", createTime=")
				.append(createTime).append(", updateTime=").append(updateTime).append("]").toString();
	}
}
