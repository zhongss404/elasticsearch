package com.example.demo.config;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dashuai on 2018/1/9.
 * Jest客户端配置
 *
 */
@Configuration
public class JestConfig {
    @Autowired
    private ConfigProperties properties;

    @Bean
    public JestHttpClient jestHttpClient(){
        JestHttpClient client;
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder(
                properties.getMaster()).multiThreaded(true).build());
        client = (JestHttpClient) factory.getObject();
        return client;
    }
}
