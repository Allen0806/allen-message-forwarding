package com.allen.message.forwarding.metadata.service.impl;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.allen.message.forwarding.metadata.dao.MessageConfigDAO;
import com.allen.message.forwarding.metadata.model.MessageConfigDTO;
import com.allen.message.forwarding.metadata.model.MessageConfigVO;
import com.allen.message.forwarding.metadata.service.MessageConfigService;
import com.allen.tool.validation.ValidationGroup.Update;

/**
 * 消息配置管理服务层接口实现类
 *
 * @author Allen
 * @date 2020年10月19日
 * @since 1.0.0
 */
public class MessageConfigServiceImpl implements MessageConfigService {

	/**
	 * 日志纪录器
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageConfigServiceImpl.class);

	/**
	 * DAO层实例
	 */
	@Autowired
	private MessageConfigDAO messageConfigDAO;

	@Override
	public void save(MessageConfigVO messageConfigVO) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(MessageConfigVO messageConfigVO) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateBusinessLineName(String businessLineId, String businessLineName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSourceSystemName(Integer sourceSystemId, String sourceSystemName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Long id, String updatedBy) {
		// TODO Auto-generated method stub

	}

	@Override
	public MessageConfigVO get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count(Integer sourceSystemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MessageConfigVO> list4Paging(Integer sourceSystemId, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageConfigDTO getByMessageId(Integer messageId) {
		// TODO Auto-generated method stub
		return null;
	}

}
