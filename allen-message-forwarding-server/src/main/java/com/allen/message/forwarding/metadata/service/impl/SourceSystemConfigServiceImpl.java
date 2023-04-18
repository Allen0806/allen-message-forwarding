package com.allen.message.forwarding.metadata.service.impl;


import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.metadata.dao.SourceSystemConfigDAO;
import com.allen.message.forwarding.metadata.model.*;
import com.allen.message.forwarding.metadata.service.BusinessLineConfigService;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.message.forwarding.metadata.service.SourceSystemConfigService;
import com.allen.tool.exception.CustomBusinessException;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.PagingQueryResult;
import com.allen.tool.string.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 消息来源系统配置信息管理Service层接口实现类
 *
 * @author Allen
 * @date Jul 20, 2020
 * @since 1.0.0
 */
@Slf4j
@Service
@RefreshScope
public class SourceSystemConfigServiceImpl implements SourceSystemConfigService {

    /**
     * 业务线信息服务
     */
    @Resource
    private BusinessLineConfigService businessLineConfigService;

    /**
     * 消息配置管理服务实例
     */
    @Resource
    private MessageConfigService messageConfigService;

    /**
     * 消息来源系统配置信息DAO层接口实例
     */
    @Resource
    private SourceSystemConfigDAO sourceSystemConfigDAO;

    @Transactional
    @Override
    public void save(SourceSystemConfigVO sourceSystemConfigVO) {
        AmfSourceSystemConfigDO sourceSystemConfigDO = toDataObject(sourceSystemConfigVO);
        if (StringUtil.isBlank(sourceSystemConfigDO.getUpdatedBy())) {
            sourceSystemConfigDO.setUpdatedBy(sourceSystemConfigDO.getCreatedBy());
        }
        int count = sourceSystemConfigDAO.save(sourceSystemConfigDO);
        if (count == 0) {
            log.error("保存来源系统信息失败，来源系统名称：{}，创建人：{}", sourceSystemConfigDO.getSourceSystemName(),
                    sourceSystemConfigDO.getCreatedBy());
            throw new CustomBusinessException(ResultStatuses.MF_0201);
        }
        log.info("保存来源系统信息成功，来源系统名称：{}，创建人：{}", sourceSystemConfigDO.getSourceSystemName(),
                sourceSystemConfigDO.getCreatedBy());
    }

    @Transactional
    @Override
    public void update(SourceSystemConfigVO sourceSystemConfigVO) {
        Long id = sourceSystemConfigVO.getId();
        String newSourceSystemName = sourceSystemConfigVO.getSourceSystemName();
        AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(id);
        if (sourceSystemConfigDO == null) {
            log.error("不存在对应的来源系统信息，来源系统主键：{}", id);
            throw new CustomBusinessException(ResultStatuses.MF_0202);
        }
        if (sourceSystemConfigDO.getSourceSystemName().equals(newSourceSystemName)) {
            log.info("来源系统名称没有变化，不进行更新操作，来源系统名称：{}", newSourceSystemName);
            return;
        }
        String updatedBy = sourceSystemConfigVO.getUpdatedBy();
        Integer sourceSystemId = sourceSystemConfigDO.getSourceSystemId();
        sourceSystemConfigDO.setSourceSystemName(newSourceSystemName);
        sourceSystemConfigDO.setUpdatedBy(updatedBy);
        int count = sourceSystemConfigDAO.update(sourceSystemConfigDO);
        if (count == 0) {
            log.error("更新来源系统信息失败，来源系统ID：{}，修改人：{}", sourceSystemId, updatedBy);
            throw new CustomBusinessException(ResultStatuses.MF_0203);
        }
        // 更新消息信息的来源系统名称
        messageConfigService.updateSourceSystemName(sourceSystemId, newSourceSystemName, updatedBy);
        log.info("更新来源系统信息成功，来源系统名称：{}，修改人：{}", newSourceSystemName, updatedBy);
    }

    @Transactional
    @Override
    public void remove(Long id, String updatedBy) {
        AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(id);
        if (sourceSystemConfigDO == null) {
            log.error("不存在对应的来源系统信息，来源系统主键：{}", id);
            throw new CustomBusinessException(ResultStatuses.MF_0202);
        }
        Integer sourceSystemId = sourceSystemConfigDO.getSourceSystemId();
        String sourceSystemName = sourceSystemConfigDO.getSourceSystemName();
        MessageConfigQueryParamDTO queryParam = new MessageConfigQueryParamDTO();
        queryParam.setSourceSystemId(sourceSystemId);
        // 查询该消息来源系统是否有关联的有效的消息配置信息，如果有则不允许更新
        int messageCount = messageConfigService.count(queryParam);
        if (messageCount > 0) {
            log.info("存在消息配置信息，不能进行来源系统信息删除操作，来源系统名称：{}", sourceSystemName);
            throw new CustomBusinessException(ResultStatuses.MF_0204);
        }
        sourceSystemConfigDO.setDeleted(1);
        sourceSystemConfigDO.setUpdatedBy(updatedBy);
        int count = sourceSystemConfigDAO.update(sourceSystemConfigDO);
        if (count == 0) {
            log.error("删除来源系统信息失败，来源系统名称：{}，修改人：{}", sourceSystemName, updatedBy);
            throw new CustomBusinessException(ResultStatuses.MF_0205);
        }
        log.info("删除消息来源系统配置信息成功，来源系统名称：{}，修改人：{}", sourceSystemName, updatedBy);
    }

    @Override
    public SourceSystemConfigVO get(Long id) {
        AmfSourceSystemConfigDO sourceSystemConfigDO = sourceSystemConfigDAO.get(id);
        if (sourceSystemConfigDO == null) {
            return null;
        }
        BusinessLineConfigVO businessLineConfigDTO = businessLineConfigService
                .get(sourceSystemConfigDO.getBusinessLineConfigId());
        if (businessLineConfigDTO == null) {
            // 如果所属业务线为空，则返回null
            return null;
        }
        return toViewObject(sourceSystemConfigDO, businessLineConfigDTO);
    }

    @Override
    public PagingQueryResult<SourceSystemConfigVO> list4Paging(PagingQueryParam<SourceSystemConfigQureyParamDTO> pagingQueryParam) {
        Integer pageNo = pagingQueryParam.getPageNo();
        if (pageNo == null) {
            pageNo = 1;
            pagingQueryParam.setPageNo(pageNo);
        }
        Integer pageSize = pagingQueryParam.getPageSize();
        if (pageSize == null) {
            pageSize = 10;
            pagingQueryParam.setPageSize(pageSize);
        }
        Integer startNo = (pageNo - 1) * pageSize;
        pagingQueryParam.setStartNo(startNo);
        SourceSystemConfigQureyParamDTO queryParam = pagingQueryParam.getParam();
        if (queryParam == null) {
            queryParam = new SourceSystemConfigQureyParamDTO();
            pagingQueryParam.setParam(queryParam);
        }
        Integer quantity = sourceSystemConfigDAO.count(queryParam);
        List<AmfSourceSystemConfigDO> sourceSystemConfigDOList = sourceSystemConfigDAO.list4Paging(pagingQueryParam);
        if (sourceSystemConfigDOList == null || sourceSystemConfigDOList.isEmpty()) {
            return new PagingQueryResult<>(Collections.emptyList(), quantity, pageNo, pageSize);
        }
        List<SourceSystemConfigVO> sourceSystemConfigVOList = new ArrayList<>();
        for (AmfSourceSystemConfigDO sourceSystemConfigDO : sourceSystemConfigDOList) {
            Long businessLineConfigId = sourceSystemConfigDO.getBusinessLineConfigId();
            BusinessLineConfigVO businessLineConfigVO = businessLineConfigService.get(businessLineConfigId);
            SourceSystemConfigVO sourceSystemConfigVO = toViewObject(sourceSystemConfigDO, businessLineConfigVO);
            sourceSystemConfigVOList.add(sourceSystemConfigVO);
        }
        return new PagingQueryResult<>(sourceSystemConfigVOList, quantity, pageNo, pageSize);
    }

    /**
     * 将VO对象转换为DO对象
     *
     * @param sourceSystemConfigVO VO对象
     * @return DO对象
     */
    private AmfSourceSystemConfigDO toDataObject(SourceSystemConfigVO sourceSystemConfigVO) {
        if (sourceSystemConfigVO == null) {
            return null;
        }
        AmfSourceSystemConfigDO sourceSystemConfigDO = new AmfSourceSystemConfigDO();
        BeanUtils.copyProperties(sourceSystemConfigVO, sourceSystemConfigDO);
        return sourceSystemConfigDO;
    }

    /**
     * 将DO对象转换为VO对象
     *
     * @param sourceSystemConfigDO DO对象
     * @param businessLineConfigVO 业务线信息对象
     * @return VO对象
     */
    private SourceSystemConfigVO toViewObject(AmfSourceSystemConfigDO sourceSystemConfigDO, BusinessLineConfigVO businessLineConfigVO) {
        if (sourceSystemConfigDO == null) {
            return null;
        }
        SourceSystemConfigVO sourceSystemConfigVO = new SourceSystemConfigVO();
        BeanUtils.copyProperties(sourceSystemConfigDO, sourceSystemConfigVO);
        if (businessLineConfigVO != null) {
            sourceSystemConfigVO.setBusinessLineId(businessLineConfigVO.getBusinessLineId());
            sourceSystemConfigVO.setBusinessLineName(businessLineConfigVO.getBusinessLineName());
        }
        return sourceSystemConfigVO;
    }

}
