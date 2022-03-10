package com.allen.message.forwarding.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * Ribbon配置类
 *
 * @author Allen
 * @date 2020年5月12日
 * @since 1.0.0
 */
@Configuration
public class RibbonConfig {
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
