package com.allen.message.forwarding.metadata.dao;

import com.allen.message.forwarding.metadata.model.AmfMessageConfigDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息配置管理DAO层接口
 *
 * @author Allen
 * @date 2020年10月19日
 * @since 1.0.0
 */
public interface MessageConfigDAO {

    /**
     * 新增消息配置信息
     *
     * @param messageConfigDO 消息配置信息
     * @return 新增成功的数量
     */
    int save(AmfMessageConfigDO messageConfigDO);

    /**
     * 修改消息配置信息
     *
     * @param messageConfigDO 消息配置信息
     * @return 修改成功的数量
     */
    int update(AmfMessageConfigDO messageConfigDO);

    /**
     * 根据业务线ID更新消息配置信息的业务线名称
     *
     * @param businessLineId   业务线ID
     * @param businessLineName 业务线名称
     * @param updatedBy        修改人
     * @return 更新的数量
     */
    int updateBusinessLineName(@Param("businessLineId") String businessLineId,
                               @Param("businessLineName") String businessLineName, @Param("updatedBy") String updatedBy);

    /**
     * 根据来源系统ID更新来源系统名称
     *
     * @param sourceSystemId   来源系统ID
     * @param sourceSystemName 来源系统名称
     * @param updatedBy        修改人
     * @return 更新的数量
     */
    int updateSourceSystemName(@Param("sourceSystemId") Integer sourceSystemId,
                               @Param("sourceSystemName") String sourceSystemName, @Param("updatedBy") String updatedBy);

    /**
     * 根据主键ID获取消息配置信息
     *
     * @param id 主键ID
     * @return 消息配置信息
     */
    AmfMessageConfigDO get(Long id);

    /**
     * 根据消息配置ID获取有效的消息配置信息
     *
     * @param messageId 消息配置ID
     * @return 消息配置信息
     */
    AmfMessageConfigDO getByMessageId(Integer messageId);

    /**
     * 统计给定的业务线下的未删除的消息配置数量
     *
     * @param businessLineId 业务线Id
     * @return 消息配置数量
     */
    int countByBusinessLineId(String businessLineId);

    /**
     * 分页查询给定的业务线下的未删除的消息配置信息
     *
     * @param businessLineId 业务线Id
     * @param startNo        起始行号
     * @param pageSize       每页行数
     * @return 分页查询结果
     */
    List<AmfMessageConfigDO> listByBusinessLineId4Paging(@Param("businessLineId") String businessLineId,
                                                         @Param("startNo") int startNo, @Param("pageSize") int pageSize);

    /**
     * 统计给定的来源系统下的未删除的消息配置数量
     *
     * @param sourceSystemId 来源系统Id
     * @return 消息配置数量
     */
    int countBySourceSystemId(Integer sourceSystemId);

    /**
     * 分页查询给定的来源系统下的未删除的消息配置信息
     *
     * @param sourceSystemId 来源系统Id
     * @param startNo        起始行号
     * @param pageSize       每页行数
     * @return 分页查询结果
     */
    List<AmfMessageConfigDO> listBySourceSystemId4Paging(@Param("sourceSystemId") Integer sourceSystemId,
                                                         @Param("startNo") int startNo, @Param("pageSize") int pageSize);
}
