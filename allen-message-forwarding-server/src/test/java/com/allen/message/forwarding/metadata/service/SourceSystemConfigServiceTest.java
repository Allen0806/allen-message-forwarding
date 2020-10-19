package com.allen.message.forwarding.metadata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.allen.message.forwarding.metadata.model.SourceSystemConfigVO;

/**
 * 消息来源系统服务层单元测试类
 * 
 * @author Allen
 * @date 2020-8-14
 * @since 1.0.0
 *
 */
@SpringBootTest
public class SourceSystemConfigServiceTest {

	@Autowired
	private SourceSystemConfigService sourceSystemConfigService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.allen.message.forwarding.metadata.service.impl.SourceSystemConfigServiceImpl#save(com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO)}.
	 */
	@Test
	final void testSave() {
		SourceSystemConfigVO sourceSystemConfigDTO = new SourceSystemConfigVO();
		sourceSystemConfigDTO.setBusinessLineId("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		sourceSystemConfigDTO.setBusinessLineName("消息系统");
		sourceSystemConfigDTO.setSourceSystemName("子系统2");
		sourceSystemConfigDTO.setCreatedBy("admin");
		sourceSystemConfigDTO.setUpdatedBy("admin");

		sourceSystemConfigService.save(sourceSystemConfigDTO);
	}

	/**
	 * Test method for
	 * {@link com.allen.message.forwarding.metadata.service.impl.SourceSystemConfigServiceImpl#update(com.allen.message.forwarding.metadata.model.AmfSourceSystemConfigDO)}.
	 */
//	@Test
	final void testUpdate() {
		SourceSystemConfigVO sourceSystemConfigDTO = new SourceSystemConfigVO();
		sourceSystemConfigDTO.setId(1L);
		sourceSystemConfigDTO.setSourceSystemName("客户系统");
		sourceSystemConfigDTO.setUpdatedBy("allen");
		sourceSystemConfigService.update(sourceSystemConfigDTO);
	}

	/**
	 * Test method for
	 * {@link com.allen.message.forwarding.metadata.service.impl.SourceSystemConfigServiceImpl#remove(java.lang.Long, java.lang.String)}.
	 */
//	@Test
	final void testRemove() {
		sourceSystemConfigService.remove(1L, "admin");
	}

	/**
	 * Test method for
	 * {@link com.allen.message.forwarding.metadata.service.impl.SourceSystemConfigServiceImpl#get(java.lang.Long)}.
	 */
//	@Test
	final void testGet() {
		SourceSystemConfigVO sourceSystemConfigDTO = sourceSystemConfigService.get(1L);
		assertEquals("信贷系统", sourceSystemConfigDTO.getBusinessLineName());
	}

	/**
	 * Test method for
	 * {@link com.allen.message.forwarding.metadata.service.impl.SourceSystemConfigServiceImpl#count(java.lang.Long)}.
	 */
//	@Test
	final void testCount() {
		int count = sourceSystemConfigService.count(1L);
		assertEquals(1, count);
	}

	/**
	 * Test method for
	 * {@link com.allen.message.forwarding.metadata.service.impl.SourceSystemConfigServiceImpl#listByBusinessLineId4Paging(java.lang.Long, int, int)}.
	 */
//	@Test
	final void testListByBusinessLineId4Paging() {
		List<SourceSystemConfigVO> list = sourceSystemConfigService.list4Paging(1L, 1, 10);
		assertEquals(1, list.size());
	}

}
