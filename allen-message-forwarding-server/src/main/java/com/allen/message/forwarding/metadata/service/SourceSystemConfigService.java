package com.allen.message.forwarding.metadata.service;

import com.allen.message.forwarding.metadata.model.SourceSystemConfigQureyParamDTO;
import com.allen.message.forwarding.metadata.model.SourceSystemConfigVO;
import com.allen.tool.param.PagingQueryParam;
import com.allen.tool.result.PagingQueryResult;
import com.allen.tool.validation.ValidationGroup;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

/**
 * 消息来源系统配置信息管理Service层接口
 *
 * @author Allen
 * @date Jul 20, 2020
 * @since 1.0.0
 */
@Validated
public interface SourceSystemConfigService {

    /**
     * 新增消息来源系统配置信息
     *
     * @param sourceSystemConfigVO 消息来源系统配置信息
     */
    @Validated({ValidationGroup.Insert.class, Default.class})
    void save(@NotNull(message = "消息来源系统配置信息不能为空") @Valid SourceSystemConfigVO sourceSystemConfigVO);

    /**
     * 修改消息来源系统配置信息
     *
     * @param sourceSystemConfigVO 消息来源系统配置信息
     */
    @Validated({ValidationGroup.Update.class, Default.class})
    void update(@NotNull(message = "消息来源系统配置信息不能为空") @Valid SourceSystemConfigVO sourceSystemConfigVO);

    /**
     * 根据主键ID删除消息来源系统配置信息，逻辑删除。如果有对应的消息配置，则不允许删除
     *
     * @param id        主键ID
     * @param updatedBy 修改人ID
     */
    void remove(@NotNull(message = "消息来源系统配置信息主键ID不能为空") Long id, @NotNull(message = "修改人ID不能为空") String updatedBy);

    /**
     * 根据主键ID获取未删除的消息来源系统配置信息
     *
     * @param id 主键ID
     * @return 消息来源系统配置信息
     */
    SourceSystemConfigVO get(@NotNull(message = "消息来源系统配置信息主键ID不能为空") Long id);

    /**
     * 分页查询未删除的消息来源系统配置信息
     *
     * @param pagingQueryParam 分页查询参数
     * @return 分页查询结果
     */
    PagingQueryResult<SourceSystemConfigVO> list4Paging(@NotNull(message = "消息来源系统配置信息分页查询参数不能为空") PagingQueryParam<SourceSystemConfigQureyParamDTO> pagingQueryParam);
}
