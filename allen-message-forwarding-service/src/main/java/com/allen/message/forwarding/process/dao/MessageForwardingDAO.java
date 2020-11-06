package com.allen.message.forwarding.process.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.allen.message.forwarding.process.model.AmfMessageForwardingDO;
import com.allen.message.forwarding.process.model.ForwardingQueryParamDTO;

/**
 * 消息管理DAO层
 * 
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
public interface MessageForwardingDAO {

	/**
	 * 批量新增消息转发信息
	 * 
	 * @param messageForwardings 消息转发信息
	 * @return 新增数量
	 */
	int save(List<AmfMessageForwardingDO> messageForwardings);

	/**
	 * 修改消息转发信息
	 * 
	 * @param messageForwardingDO 消息转发信息
	 * @return 修改数量
	 */
	int update(AmfMessageForwardingDO messageForwardingDO);

	/**
	 * 迁移消息转发信息，仅限插入到历史表
	 * 
	 * @param deadline 截止时间，即只迁移创建时间小于给定时间的数据
	 * @return 迁移数量
	 */
	int migrate(LocalDateTime deadline);

	/**
	 * 删除消息转发信息，仅删除迁移后的数据
	 * 
	 * @param deadline 截止时间，即只删除创建时间小于给定时间的数据
	 * @return 删除的数量
	 */
	int remove(LocalDateTime deadline);

	/**
	 * 获取消息转发信息
	 * 
	 * @param id 主键
	 * @return 消息转发信息
	 */
	AmfMessageForwardingDO getById(Long id);

	/**
	 * 获取消息转发信息
	 * 
	 * @param messageNo    消息流水号
	 * @param forwardingId 消息转发配置主键
	 * @return 消息转发信息
	 */
	AmfMessageForwardingDO get(@Param("messageNo") String messageNo, @Param("forwardingId") Long forwardingId);

	/**
	 * 根据给定参数查询消息转发信息数量
	 * 
	 * @param queryParam 查询参数
	 * @return 消息转发信息数量
	 */
	int count(ForwardingQueryParamDTO queryParam);

	/**
	 * 根据给定参数查询消息转发信息
	 * 
	 * @param queryParam 查询参数
	 * @return 消息转发信息
	 */
	List<AmfMessageForwardingDO> list(ForwardingQueryParamDTO queryParam);
}
