package com.allen.message.forwarding.metadata.service;

import java.util.List;

import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;

/**
 * 消息来源系统配置信息管理Service层接口
 *
 * @author Allen
 * @date Jul 20, 2020
 * @since 1.0.0
 */
public interface SourceSystemConfigService {

	/**
	 * 新增消息来源系统配置信息
	 * 
	 * @param sourceSystemConfigDO 消息来源系统配置信息
	 */
	void save(AmfSourceSystemConfigDO sourceSystemConfigDO);

	/**
	 * 修改消息来源系统配置信息
	 * 
	 * @param sourceSystemConfigDO
	 */
	void update(AmfSourceSystemConfigDO sourceSystemConfigDO);

	/**
	 * 根据主键ID删除消息来源系统配置信息，逻辑删除。如果有对应的消息配置，则不允许删除
	 * 
	 * @param id        主键ID
	 * @param updatedBy 修改人ID
	 */
	void remove(Long id, String updatedBy);

	/**
	 * 根据主键ID获取消息来源系统配置信息
	 * 
	 * @param id 主键ID
	 * @return 消息来源系统配置信息
	 */
	AmfSourceSystemConfigDO get(Long id);

	/**
	 * 根据业务线ID统计消息来源系统配置信息数量
	 * 
	 * @param businessLineId 业务线ID
	 * @return 消息来源系统配置信息数量
	 */
	int count(String businessLineId);

	/**
	 * 根据业务线ID分页查询消息来源系统配置信息
	 * 
	 * @param businessLineId 业务线ID
	 * @param pageNo         当前页数
	 * @param pageSize       每页行数
	 * @return 分页查询结果
	 */
	List<AmfSourceSystemConfigDO> listByBusinessLineId4Paging(String businessLineId, int pageNo, int pageSize);
}
