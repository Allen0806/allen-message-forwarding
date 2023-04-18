package com.allen.message.forwarding.metadata.dao;

import com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO;
import com.allen.message.forwarding.metadata.model.SourceSystemConfigQureyParamDTO;
import com.allen.tool.param.PagingQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
     * 根据主键ID获取未删除的消息来源系统配置信息
     *
     * @param id 主键ID
     * @return 消息来源系统配置信息
     */
    AmfSourceSystemConfigDO get(Long id);

    /**
     * 统计未删除的消息来源系统配置信息数量
     *
     * @param queryParam 查询参数
     * @return 消息来源系统配置信息数量
     */
    int count(SourceSystemConfigQureyParamDTO queryParam);

    /**
     * 分页查询未删除的消息来源系统配置信息
     *
     * @param pagingQueryParam 分页查询参数
     * @return 分页查询结果
     */
    List<AmfSourceSystemConfigDO> list4Paging(PagingQueryParam<SourceSystemConfigQureyParamDTO> pagingQueryParam);
}
