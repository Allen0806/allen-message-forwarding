package com.allen.message.forwarding.process.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.allen.message.forwarding.process.model.AmfMessageDO;
import com.allen.message.forwarding.process.model.MessageQueryParamDTO;

/**
 * 消息管理DAO层
 * 
 * @author Allen
 * @date 2020年11月3日
 * @since 1.0.0
 */
public interface MessageDAO {

	/**
	 * 新增消息信息
	 * 
	 * @param messageDO 消息信息
	 * @return 新增数量
	 */
	int save(AmfMessageDO messageDO);

	/**
	 * 增加消息转发成功数量，每次加1
	 * 
	 * @param messageNo 消息流水号
	 * @return 修改数量
	 */
	int updateSucessAmount(String messageNo);

	/**
	 * 迁移消息信息，仅限插入到历史表
	 * 
	 * @param deadline 截止时间，即只迁移创建时间小于给定时间的数据
	 * @return 迁移数量
	 */
	int migrate(LocalDateTime deadline);

	/**
	 * 删除消息信息，仅删除迁移后的数据
	 * 
	 * @param deadline 截止时间，即只删除创建时间小于给定时间的数据
	 * @return 删除的数量
	 */
	int remove(LocalDateTime deadline);

	/**
	 * 获取消息信息
	 * 
	 * @param messageNo 消息流水号
	 * @return 消息信息
	 */
	AmfMessageDO get(String messageNo);

	/**
	 * 根据给定参数查询消息信息数量
	 * 
	 * @param queryParam 查询参数
	 * @return 消息信息数量
	 */
	int count(MessageQueryParamDTO queryParam);

	/**
	 * 根据给定参数查询消息信息
	 * 
	 * @param queryParam 查询参数
	 * @return 消息信息
	 */
	List<AmfMessageDO> list(MessageQueryParamDTO queryParam);
}
