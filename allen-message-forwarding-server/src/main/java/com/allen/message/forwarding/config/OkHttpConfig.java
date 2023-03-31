package com.allen.message.forwarding.config;

import feign.Feign;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * OkHttpClient 配置类
 *
 * @author luoxuetong
 * @date 2022-08-01
 */
@Configuration
@ConditionalOnClass(Feign.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
@Slf4j
@RefreshScope
public class OkHttpConfig {

    /**
     * 连接超时时间
     */
    @Value("${ok.http.connect-timeout:10}")
    private Integer connectTimeout;

    /**
     * 读取超时时间
     */
    @Value("${ok.http.read-timeout:10}")
    private Integer readTimeout;

    /**
     * 写入超时时间
     */
    @Value("${ok.http.write-timeout:10}")
    private Integer writeTimeout;

    /**
     * 最大连接数
     */
    @Value("${ok.http.max-idle-connections:200}")
    private Integer maxIdleConnections;

    /**
     * 连接存活时间，单位：秒
     */
    @Value("${ok.http.keep-alive-duration:300}")
    private Integer keepAliveDuration;

    /**
     * 连接池配置
     *
     * @return 连接池
     */
    @Bean
    public ConnectionPool connectionPool() {
        // 最大连接数、连接存活时间、存活时间单位（分钟）
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
    }

    /**
     * X509 证书来验证远端的安全套接字
     *
     * @return
     */
    @Bean
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    /**
     * 证书配置
     *
     * @return
     */
    @Bean
    public SSLSocketFactory sslSocketFactory() {
        try {
            // 信任任何链接，与服务器保持一致，SSL算法或者TSL算法
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("创建SSLSocketFactory异常", e);
        }
        return null;
    }


    /**
     * OkHttp 客户端配置
     *
     * @return OkHttp 客户端配
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(chain -> {
                    Request build = chain.request().newBuilder().addHeader("Connection", "close").build();
                    return chain.proceed(build);
                })
                .connectionPool(connectionPool())             //连接池
                .connectTimeout(connectTimeout, TimeUnit.SECONDS) // 连接超时时间
                .readTimeout(readTimeout, TimeUnit.SECONDS) // 读取超时时间
                .writeTimeout(writeTimeout, TimeUnit.SECONDS) // 写入超时时间
                .followRedirects(true) // 是否允许重定向
                .build();
    }
}
