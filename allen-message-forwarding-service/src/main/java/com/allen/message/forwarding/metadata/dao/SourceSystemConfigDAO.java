package com.allen.message.forwarding.metadata.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;

/**
 * 消息来源系统配置信息DAO层接口
 *
 * @author Allen
 * @date Jul 15, 2020
 * @since 1.0.0
 */
@Mapper
public interface SourceSystemConfigDAO {

	/**
	 * 新增消息来源系统配置信息
	 * 
	 * @param sourceSystemConfigDO 消息来源系统配置信息
	 * @return 新增成功数量
	 */
	int save(AmfSourceSystemConfigDO sourceSystemConfigDO);

	/**
	 * 修改消息来源系统配置信息
	 * 
	 * @param sourceSystemConfigDO
	 * @return 修改成功数量
	 */
	int update(AmfSourceSystemConfigDO sourceSystemConfigDO);

	/**
	 * 根据主键ID删除消息来源系统配置信息，逻辑删除
	 * 
	 * @param id 主键ID
	 * @return 删除数量
	 */
	int remove(Long id);

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
	 * @param startNo        起始行号
	 * @param pageSize       每页行数
	 * @return 分页查询结果
	 */
	List<AmfSourceSystemConfigDO> listByBusinessLineId4Paging(@Param("businessLineId") String businessLineId,
			@Param("startNo") int startNo, @Param("pageSize") int pageSize);
}
