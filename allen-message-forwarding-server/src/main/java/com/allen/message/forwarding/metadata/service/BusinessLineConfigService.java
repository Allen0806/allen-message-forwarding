package com.allen.message.forwarding.metadata.service;

import com.allen.message.forwarding.metadata.model.BusinessLineConfigQueryParamDTO;
import com.allen.message.forwarding.metadata.model.BusinessLineConfigVO;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.PagingQueryResult;
import com.allen.tool.validation.ValidationGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.List;

/**
 * 消息所属业务线配置信息管理Service层接口
 *
 * @author Allen
 * @date 2020年10月16日
 * @since 1.0.0
 */
@Validated
public interface BusinessLineConfigService {

    /**
     * 新增消息所属业务线配置信息
     *
     * @param businessLineConfigVO 消息所属业务线配置信息
     */
    @Validated({ValidationGroup.Insert.class, Default.class})
    void save(@NotNull(message = "消息所属业务线配置信息不能为空") @Valid BusinessLineConfigVO businessLineConfigVO);

    /**
     * 修改消息所属业务线配置信息
     *
     * @param businessLineConfigVO 消息所属业务线配置信息
     */
    @Validated({ValidationGroup.Update.class, Default.class})
    void update(@NotNull(message = "消息所属业务线配置信息不能为空") @Valid BusinessLineConfigVO businessLineConfigVO);

    /**
     * 根据主键ID删除消息所属业务线配置信息，逻辑删除。如果有对应的消息来源系统配置信息，则不允许删除
     *
     * @param id        主键ID
     * @param updatedBy 修改人ID
     */
    void remove(@NotNull(message = "消息所属业务线配置信息主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy);

    /**
     * 根据主键ID获取未标记为删除的所属业务线配置信息
     *
     * @param id 主键ID
     * @return 所属业务线配置信息
     */
    BusinessLineConfigVO get(@NotNull(message = "消息所属业务线配置信息主键ID不能为空") Long id);

    /**
     * 根据业务线ID获取未标记为删除的所属业务线配置信息
     *
     * @param businessLineId 业务线ID
     * @return 所属业务线配置信息
     */
    BusinessLineConfigVO getByBusinessLineId(@NotNull(message = "消息所属业务线配置信息业务线ID不能为空") String businessLineId);

    /**
     * 根据业务线ID或（和）业务线名称模糊查询未删除的业务线信息，业务线ID和业务线名称不可同时为空，根据最左匹配规则查询
     *
     * @param queryParam 查询参数
     * @return 查询到的业务线信息
     */
    List<BusinessLineConfigVO> list4Fuzzy(@NotNull(message = "消息所属业务线配置信息查询参数不能为空") BusinessLineConfigQueryParamDTO queryParam);

    /**
     * 分页查询未标记为删除的所属业务线配置信息
     *
     * @param pagingQueryParam 分页查询参数
     * @return 分页查询结果
     */
    PagingQueryResult<BusinessLineConfigVO> list4Paging(@NotNull(message = "消息所属业务线配置信息分页查询参数不能为空") PagingQueryParam<BusinessLineConfigQueryParamDTO> pagingQueryParam);
}
