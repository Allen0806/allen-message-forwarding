package com.allen.message.forwarding.config;

import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate配置类
 *
 * @author allen
 * @date 2021年5月19日
 * @since 1.0.0
 *
 */
@Configuration
public class RestTemplateConfig {

	@Resource
	private OkHttpClient okHttpClient;

	@Bean
	public RestTemplate restTemplate() {
		// 采用okHttpClient实现
		ClientHttpRequestFactory httpRequestFactory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
		return new RestTemplate(httpRequestFactory);
	}
}
