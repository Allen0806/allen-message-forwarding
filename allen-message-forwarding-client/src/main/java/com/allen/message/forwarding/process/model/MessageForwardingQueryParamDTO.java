package com.allen.message.forwarding.process.model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消息查询参数封装类
 *
 * @author Allen
 * @date 2020年5月8日
 * @since 1.0.0
 *
 */
@ApiModel("消息查询参数封装对象")
public class MessageForwardingQueryParamDTO implements java.io.Serializable {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 3309288046287419023L;

	/**
	 * 消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复
	 */
	@ApiModelProperty(value = "消息流水号，固定32位，组成规则：8为日期+4位来源系统ID+6位消息ID+14位序列号（每日从1开始），不可重复", dataType = "String", required = false)
	@Size(min = 32, max = 32, message = "消息流水号固定32位")
	private String messageNo;

	/**
	 * 消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位
	 */
	@ApiModelProperty(value = "消息关键字，方便将来追溯流水(非唯一)，比如客户号、手机号等，最长32位", dataType = "String", required = false)
	@Size(max = 32, message = "消息关键字最长32位")
	private String messageKeyword;

	/**
	 * 消息ID，即消息配置信息里的消息ID，固定6位
	 */
	@ApiModelProperty(value = "消息ID，消息配置信息里的消息ID，固定6位", dataType = "Integer", required = false)
	@Range(min = 100000, max = 999999, message = "消息ID取值范围为100000~999999")
	private Integer messageId;

	/**
	 * 消息转发配置主键
	 */
	@ApiModelProperty(value = "消息转发配置主键", dataType = "Long", required = false)
	private Long forwardingId;

	/**
	 * 转发处理状态：0-处理中，1-重试中，2-已完成
	 */
	@ApiModelProperty(value = "转发处理状态", dataType = "Integer", required = false)
	private Integer forwardingStatus;

	/**
	 * 转发结果：0-失败，1-成功
	 */
	@ApiModelProperty(value = "转发结果", dataType = "Integer", required = false)
	private Integer forwardingResult;

	/**
	 * 回调处理状态：0-处理中，1-重试中，2-已完成
	 */
	@ApiModelProperty(value = "回调处理状态", dataType = "Integer", required = false)
	private Integer callbackStatus;

	/**
	 * 回调结果：0-失败，1-成功
	 */
	@ApiModelProperty(value = "回调结果", dataType = "Integer", required = false)
	private Integer callbackResult;

	/**
	 * 通过Http接口转发消息时，设置到http header里的参数，比如接口编号等
	 */
	@ApiModelProperty(value = "起始日期，格式：yyyy-MM-dd", dataType = "String", required = false)
	private String beginDate;

	/**
	 * 要转发的消息内容
	 */
	@ApiModelProperty(value = "截止日期，格式：yyyy-MM-dd", dataType = "String", required = false)
	private String endDate;

	/**
	 * 分页查询时的页数，当messageNo为空时startNo和pageNo必须给定其一，如果都给定，以startNo为准
	 */
	@ApiModelProperty(value = "分页查询时的页数，当messageNo为空时startNo和pageNo必须给定其一，如果都给定，以startNo为准", dataType = "Integer", required = false)
	private Integer pageNo;

	/**
	 * 分页查询时起始行号，从0开始。当messageNo为空时startNo和pageNo必须给定其一
	 */
	@ApiModelProperty(value = "分页查询时起始行号，从0开始。当messageNo为空时必须给定", dataType = "Integer", required = false)
	private Integer startNo;

	/**
	 * 分页查询时每页行数，最小值为1，最大值为1000。当startNo不为空时必须给定
	 */
	@ApiModelProperty(value = "分页查询时每页行数，最小值为1，最大值为1000。当startNo不为空时必须给定", dataType = "Integer", required = false)
	@Range(min = 1, max = 1000, message = "每页行数取值范围为1～1000")
	private Integer pageSize;

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

	public Long getForwardingId() {
		return forwardingId;
	}

	public void setForwardingId(Long forwardingId) {
		this.forwardingId = forwardingId;
	}

	public Integer getForwardingStatus() {
		return forwardingStatus;
	}

	public void setForwardingStatus(Integer forwardingStatus) {
		this.forwardingStatus = forwardingStatus;
	}

	public Integer getForwardingResult() {
		return forwardingResult;
	}

	public void setForwardingResult(Integer forwardingResult) {
		this.forwardingResult = forwardingResult;
	}

	public Integer getCallbackStatus() {
		return callbackStatus;
	}

	public void setCallbackStatus(Integer callbackStatus) {
		this.callbackStatus = callbackStatus;
	}

	public Integer getCallbackResult() {
		return callbackResult;
	}

	public void setCallbackResult(Integer callbackResult) {
		this.callbackResult = callbackResult;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getStartNo() {
		return startNo;
	}

	public void setStartNo(Integer startNo) {
		this.startNo = startNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("ForwardingQueryParam[").append("messageNo=").append(messageNo)
				.append(", messageKeyword=").append(messageKeyword).append(", messageId=").append(messageId)
				.append(", forwardingId=").append(forwardingId).append(", forwardingStatus=").append(forwardingStatus)
				.append(", forwardingResult=").append(forwardingResult).append(", callbackStatus=")
				.append(callbackStatus).append(", callbackResult=").append(callbackResult).append(", beginDate=")
				.append(beginDate).append(", endDate=").append(endDate).append(", startNo=").append(startNo)
				.append(", pageSize=").append(pageSize).append("]").toString();
	}
}
