package com.allen.message.forwarding.constant;

import com.allen.tool.result.ResultStatus;

/**
 * 消息转发系统状态码定义常量类
 *
 * @author Allen
 * @date Aug 3, 2020
 * @since 1.0.0
 */
public class ResultStatuses {

	/*************** 公共状态码定义 ***************/
	public static final ResultStatus MF_0001 = new ResultStatus("MF0001", "当前页数或每页行数不能小于1");

	/*************** 业务线管理状态码定义 ***************/
	public static final ResultStatus MF_0101 = new ResultStatus("MF0101", "保存业务线信息失败");
	public static final ResultStatus MF_0102 = new ResultStatus("MF0102", "不存在对应的业务线信息");
	public static final ResultStatus MF_0103 = new ResultStatus("MF0103", "更新业务线信息失败");
	public static final ResultStatus MF_0104 = new ResultStatus("MF0104", "存在来源系统信息，不能删除业务线信息");
	public static final ResultStatus MF_0105 = new ResultStatus("MF0105", "删除业务线信息失败");
	public static final ResultStatus MF_0106 = new ResultStatus("MF0106", "业务线ID和业务线名称不能同时为空");
	public static final ResultStatus MF_0107 = new ResultStatus("MF0107", "业务线ID中不能包含%");
	public static final ResultStatus MF_0108 = new ResultStatus("MF0108", "业务线名称中不能包含%");

	/*************** 来源系统管理状态码定义 ***************/
	public static final ResultStatus MF_0201 = new ResultStatus("MF0201", "保存来源系统信息失败");
	public static final ResultStatus MF_0202 = new ResultStatus("MF0202", "不存在对应的来源系统信息");
	public static final ResultStatus MF_0203 = new ResultStatus("MF0203", "更新来源系统信息失败");
	public static final ResultStatus MF_0204 = new ResultStatus("MF0204", "存在消息配置信息，不能进删除来源系统信息");
	public static final ResultStatus MF_0205 = new ResultStatus("MF0205", "删除来源系统信息失败");

	/*************** 消息配置管理状态码定义 ***************/
	public static final ResultStatus MF_0301 = new ResultStatus("MF0301", "保存消息配置信失败");
	public static final ResultStatus MF_0302 = new ResultStatus("MF0302", "不存在对应的消息配置信息");
	public static final ResultStatus MF_0303 = new ResultStatus("MF0303", "更新消息配置信失败");
	public static final ResultStatus MF_0304 = new ResultStatus("MF0304", "存在消息转发信息，不能删除消息配置信息");
	public static final ResultStatus MF_0305 = new ResultStatus("MF0305", "删除消息配置信息失败");
	public static final ResultStatus MF_0306 = new ResultStatus("MF0306", "获取消息配置信息异常");

	/*************** 消息转发配置管理状态码定义 ***************/
	public static final ResultStatus MF_0401 = new ResultStatus("MF0401", "保存消息转发配置信息失败");
	public static final ResultStatus MF_0402 = new ResultStatus("MF0402", "不存在对应的消息转发配置信息");
	public static final ResultStatus MF_0403 = new ResultStatus("MF0403", "更新消息转发配置信失败");
	public static final ResultStatus MF_0404 = new ResultStatus("MF0404", "删除消息转发配置信息失败");

	/*************** 消息转发处理状态码定义 ***************/
	public static final ResultStatus MF_1001 = new ResultStatus("MF1001", "未获取到消息配置信息或消息配置信息有误");
	public static final ResultStatus MF_1002 = new ResultStatus("MF1002", "传入的业务线ID及来源系统ID与消息配置信息中的不匹配");
	public static final ResultStatus MF_1003 = new ResultStatus("MF1003", "保存消息信息失败");
	public static final ResultStatus MF_1004 = new ResultStatus("MF1004", "消息转发明细信息为空");
	public static final ResultStatus MF_1005 = new ResultStatus("MF1005", "保存消息转发明细信息失败");
	public static final ResultStatus MF_1006 = new ResultStatus("MF1006", "消息topic为空");
	public static final ResultStatus MF_1007 = new ResultStatus("MF1007", "消息为空");
	public static final ResultStatus MF_1008 = new ResultStatus("MF1008", "消息发送异常");
	public static final ResultStatus MF_1009 = new ResultStatus("MF1009", "更新消息转发明细失败");
	public static final ResultStatus MF_1010 = new ResultStatus("MF1010", "消息转发异常");
	public static final ResultStatus MF_1011 = new ResultStatus("MF1011", "消息转发方式不正确");
	
	public static final ResultStatus MF_1012 = new ResultStatus("MF1012", "");
	public static final ResultStatus MF_1013 = new ResultStatus("MF1013", "");
	public static final ResultStatus MF_1014 = new ResultStatus("MF1014", "");
	public static final ResultStatus MF_1015 = new ResultStatus("MF1015", "");
	public static final ResultStatus MF_1016 = new ResultStatus("MF1016", "");
	public static final ResultStatus MF_1017 = new ResultStatus("MF1017", "");
	public static final ResultStatus MF_1018 = new ResultStatus("MF1018", "");
	public static final ResultStatus MF_1019 = new ResultStatus("MF1019", "");
	

}
