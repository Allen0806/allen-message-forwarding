package com.allen.message.forwarding.metadata.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.allen.message.forwarding.metadata.model.AmfBusinessLineConfigDO;

/**
 * 消息所属业务线配置信息管理DAO
 *
 * @author Allen
 * @date 2020年10月16日
 * @since 1.0.0
 */
public interface BusinessLineConfigDAO {
	/**
	 * 新增所属业务线配置信息
	 * 
	 * @param businessLineConfigDO 所属业务线配置信息
	 * @return 新增成功数量
	 */
	int save(AmfBusinessLineConfigDO businessLineConfigDO);

	/**
	 * 修改所属业务线配置信息
	 * 
	 * @param businessLineConfigDO 所属业务线配置信息
	 * @return 修改成功数量
	 */
	int update(AmfBusinessLineConfigDO businessLineConfigDO);

	/**
	 * 根据主键ID获取未标记为删除的所属业务线配置信息
	 * 
	 * @param id 主键ID
	 * @return 所属业务线配置信息
	 */
	AmfBusinessLineConfigDO get(Long id);

	/**
	 * 根据业务线ID获取未标记为删除的所属业务线配置信息
	 * 
	 * @param businessLineId 业务线ID
	 * @return 所属业务线配置信息
	 */
	AmfBusinessLineConfigDO getByBusinessLineId(String businessLineId);

	/**
	 * 根据业务线ID或（和）业务线名称模糊查询未标记为删除的业务线信息，业务线ID和业务线名称不可同时为空，根据最左匹配规则查询
	 * 
	 * @param businessLineId   业务线ID模糊信息，即可以为业务线ID的左侧部分信息
	 * @param businessLineName 业务线名称模糊信息，即可以为业务线名称的左侧部分信息
	 * @return 查询到的业务线信息
	 */
	List<AmfBusinessLineConfigDO> list4Fuzzy(@Param("businessLineId") String businessLineId,
			@Param("businessLineName") String businessLineName);

	/**
	 * 获取未标记为删除的所属业务线配置信息数量
	 * 
	 * @return 所属业务线配置信息数量
	 */
	int count();

	/**
	 * 分页查询未标记为删除的所属业务线配置信息
	 * 
	 * @param startNo  起始行号
	 * @param pageSize 每页行数
	 * @return 分页查询结果
	 */
	List<AmfBusinessLineConfigDO> list4Paging(@Param("startNo") int startNo, @Param("pageSize") int pageSize);
}
