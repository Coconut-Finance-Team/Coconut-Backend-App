package com.coconut.stock_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.List;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.coconut.stock_app.repository.elasticsearch")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.rest.uris}")
    private List<String> elasticsearchUrls; // 배열 형태로 값 주입

    @Override
    public ClientConfiguration clientConfiguration() {
        // 리스트를 배열로 변환하여 전달
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUrls.toArray(new String[1])) // 배열로 전달
                .build();
    }
}
