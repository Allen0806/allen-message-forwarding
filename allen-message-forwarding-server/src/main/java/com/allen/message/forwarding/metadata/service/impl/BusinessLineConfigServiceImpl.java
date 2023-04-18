package com.allen.message.forwarding.metadata.service.impl;

import com.allen.message.forwarding.constant.ResultStatuses;
import com.allen.message.forwarding.metadata.dao.BusinessLineConfigDAO;
import com.allen.message.forwarding.metadata.dao.SourceSystemConfigDAO;
import com.allen.message.forwarding.metadata.model.AmfBusinessLineConfigDO;
import com.allen.message.forwarding.metadata.model.BusinessLineConfigQueryParamDTO;
import com.allen.message.forwarding.metadata.model.BusinessLineConfigVO;
import com.allen.message.forwarding.metadata.model.SourceSystemConfigQureyParamDTO;
import com.allen.message.forwarding.metadata.service.BusinessLineConfigService;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息所属业务线配置信息管理Service实现类
 *
 * @author Allen
 * @date 2020年10月16日
 * @since 1.0.0
 */
@Slf4j
@Service
@RefreshScope
public class BusinessLineConfigServiceImpl implements BusinessLineConfigService {

    /**
     * 消息配置管理服务实例
     */
    @Resource
    private MessageConfigService messageConfigService;

    /**
     * 业务线信息DAO层接口实例
     */
    @Resource
    private BusinessLineConfigDAO businessLineConfigDAO;

    /**
     * 消息来源系统配置信息DAO层接口实例
     */
    @Resource
    private SourceSystemConfigDAO sourceSystemConfigDAO;

    @Transactional
    @Override
    public void save(BusinessLineConfigVO businessLineConfigVO) {
        AmfBusinessLineConfigDO businessLineConfigDO = toDataObject(businessLineConfigVO);
        businessLineConfigDO.setDeleted(0);
        // 时间可以不设置
        LocalDateTime now = LocalDateTime.now();
        if (businessLineConfigDO.getCreateTime() == null) {
            businessLineConfigDO.setCreateTime(now);
        }
        if (businessLineConfigDO.getUpdateTime() == null) {
            businessLineConfigDO.setUpdateTime(now);
        }
        if (StringUtil.isBlank(businessLineConfigDO.getUpdatedBy())) {
            businessLineConfigDO.setUpdatedBy(businessLineConfigDO.getCreatedBy());
        }
        int count = businessLineConfigDAO.save(businessLineConfigDO);
        if (count == 0) {
            log.error("保存业务线配置信息失败，业务线配置信息：{}", businessLineConfigDO);
            throw new CustomBusinessException(ResultStatuses.MF_0101);
        }
        log.info("保存业务线信息成功，业务线配置信息：{}", businessLineConfigDO);
    }

    @Transactional
    @Override
    public void update(BusinessLineConfigVO businessLineConfigVO) {
        Long id = businessLineConfigVO.getId();
        String newBusinessLineName = businessLineConfigVO.getBusinessLineName();
        AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(id);
        if (businessLineConfigDO == null) {
            log.error("不存在对应的业务线信息，业务线主键：{}", id);
            throw new CustomBusinessException(ResultStatuses.MF_0102);
        }

        if (businessLineConfigDO.getBusinessLineName().equals(newBusinessLineName)) {
            log.info("业务线名称没有变化，不进行业务线信息更新操作，业务线主键：{}，业务线名称：{}", id, newBusinessLineName);
            return;
        }
        String updatedBy = businessLineConfigVO.getUpdatedBy();
        String businessLineId = businessLineConfigDO.getBusinessLineId();
        businessLineConfigDO.setBusinessLineName(newBusinessLineName);
        businessLineConfigDO.setUpdatedBy(updatedBy);
        // 通过更新时间实现乐观锁
        int count = businessLineConfigDAO.update(businessLineConfigDO);
        if (count == 0) {
            log.error("更新业务线信息失败，业务线ID：{}，修改人：{}", businessLineId, updatedBy);
            throw new CustomBusinessException(ResultStatuses.MF_0103);
        }
        // 更新消息配置信息的业务线名称
        messageConfigService.updateBusinessLineName(businessLineId, newBusinessLineName, updatedBy);
        log.info("更新业务线信息成功，业务线名称：{}，修改人：{}", newBusinessLineName, updatedBy);
    }

    @Transactional
    @Override
    public void remove(Long id, String updatedBy) {
        AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(id);
        if (businessLineConfigDO == null) {
            log.error("不存在对应的业务线信息，业务线主键：{}", id);
            throw new CustomBusinessException(ResultStatuses.MF_0102);
        }
        String businessLineName = businessLineConfigDO.getBusinessLineName();
        SourceSystemConfigQureyParamDTO queryParam = new SourceSystemConfigQureyParamDTO();
        queryParam.setBusinessLineConfigId(id);
        // 判断是否存在关联的未标记为删除的来源系统信息，如果存在，则不允许删除
        int sourceSystemAmount = sourceSystemConfigDAO.count(queryParam);
        if (sourceSystemAmount > 0) {
            log.error("存在来源系统信息，不能进行业务线信息删除操作，业务线名称：{}", businessLineName);
            throw new CustomBusinessException(ResultStatuses.MF_0104);
        }
        businessLineConfigDO.setDeleted(1);
        businessLineConfigDO.setUpdatedBy(updatedBy);
        int count = businessLineConfigDAO.update(businessLineConfigDO);
        if (count == 0) {
            log.error("删除业务线信息失败，业务线名称：{}，删除人：{}", businessLineName, updatedBy);
            throw new CustomBusinessException(ResultStatuses.MF_0105);
        }
        log.info("删除消业务线信息成功，业务线名称：{}，删除人{}", businessLineName, updatedBy);
    }

    @Override
    public BusinessLineConfigVO get(Long id) {
        AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.get(id);
        return toViewObject(businessLineConfigDO);
    }

    @Override
    public BusinessLineConfigVO getByBusinessLineId(String businessLineId) {
        AmfBusinessLineConfigDO businessLineConfigDO = businessLineConfigDAO.getByBusinessLineId(businessLineId);
        return toViewObject(businessLineConfigDO);
    }

    @Override
    public List<BusinessLineConfigVO> list4Fuzzy(BusinessLineConfigQueryParamDTO queryParam) {
        String businessLineId = queryParam.getBusinessLineId();
        String businessLineName = queryParam.getBusinessLineName();
        if (StringUtil.isBlank(businessLineId) && StringUtil.isBlank(businessLineName)) {
            throw new CustomBusinessException(ResultStatuses.MF_0106);
        }
        if (StringUtil.isNotBlank(businessLineId)) {
            if (businessLineId.contains("%")) {
                log.error("业务线ID中不能包含%，业务线ID：{}", businessLineId);
                throw new CustomBusinessException(ResultStatuses.MF_0107);
            }
        }
        if (StringUtil.isNotBlank(businessLineName)) {
            if (businessLineName.contains("%")) {
                log.error("业务线名称中不能包含%，业务线ID：{}", businessLineId);
                throw new CustomBusinessException(ResultStatuses.MF_0108);
            }
        }
        List<AmfBusinessLineConfigDO> businessLineConfigDOList = businessLineConfigDAO.list4Fuzzy(businessLineId,
                businessLineName);
        return toViewObjects(businessLineConfigDOList);
    }

    @Override
    public PagingQueryResult<BusinessLineConfigVO> list4Paging(PagingQueryParam<BusinessLineConfigQueryParamDTO> pagingQueryParam) {
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
        BusinessLineConfigQueryParamDTO queryParam = pagingQueryParam.getParam();
        if (queryParam == null) {
            queryParam = new BusinessLineConfigQueryParamDTO();
            pagingQueryParam.setParam(queryParam);
        }
        Integer quantity = businessLineConfigDAO.count(queryParam);
        List<AmfBusinessLineConfigDO> businessLineConfigDOList = businessLineConfigDAO.list4Paging(pagingQueryParam);
        List<BusinessLineConfigVO> businessLineConfigVOList = toViewObjects(businessLineConfigDOList);
        return new PagingQueryResult<>(businessLineConfigVOList, quantity, pageNo, pageSize);
    }

    /**
     * 将VO对象转换为DO对象
     *
     * @param businessLineConfigVO VO对象
     * @return DO对象
     */
    private AmfBusinessLineConfigDO toDataObject(BusinessLineConfigVO businessLineConfigVO) {
        if (businessLineConfigVO == null) {
            return null;
        }
        AmfBusinessLineConfigDO businessLineConfigDO = new AmfBusinessLineConfigDO();
        BeanUtils.copyProperties(businessLineConfigVO, businessLineConfigDO);
        return businessLineConfigDO;
    }

    /**
     * 将DO对象转换为VO对象
     *
     * @param businessLineConfigDO DO对象
     * @return VO对象
     */
    private BusinessLineConfigVO toViewObject(AmfBusinessLineConfigDO businessLineConfigDO) {
        if (businessLineConfigDO == null) {
            return null;
        }
        BusinessLineConfigVO businessLineConfigVO = new BusinessLineConfigVO();
        BeanUtils.copyProperties(businessLineConfigDO, businessLineConfigVO);
        return businessLineConfigVO;
    }

    /**
     * 将DO列表转换为VO列表
     *
     * @param businessLineConfigDOList DO列表
     * @return VO列表
     */
    private List<BusinessLineConfigVO> toViewObjects(List<AmfBusinessLineConfigDO> businessLineConfigDOList) {
        if (businessLineConfigDOList == null || businessLineConfigDOList.isEmpty()) {
            return Collections.emptyList();
        }
        return businessLineConfigDOList.parallelStream().map(this::toViewObject).collect(Collectors.toList());
    }

}
