//package com.coconut.stock_app.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
//import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
//
//@Configuration
//@EnableElasticsearchRepositories(basePackages = "com.coconut.stock_app.repository.elasticsearch")
//public class ElasticSearchConfig extends ElasticsearchConfiguration {
//
//    @Value("${spring.elasticsearch.uris}")
//    private String elasticsearchUrl;
//
//    @Override
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo(elasticsearchUrl.replace("http://", ""))
//                .build();
//    }
//}