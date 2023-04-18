package com.allen.message.forwarding.metadata.service;

import com.allen.message.forwarding.metadata.model.BusinessLineConfigVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessLineConfigServiceTest {

    @Autowired
    private BusinessLineConfigService businessLineConfigService;

    @Test
    final void testSave(){
        BusinessLineConfigVO businessLineConfigVO = new BusinessLineConfigVO();
        businessLineConfigService.save(businessLineConfigVO);
    }

}
