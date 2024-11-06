package com.coconut.stock_app.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.coconut.stock_app.repository.on_premis",  // On-Premis Repository 위치
        entityManagerFactoryRef = "onPremisEntityManagerFactory",
        transactionManagerRef = "onPremisTransactionManager"
)
public class OnPremisDataSourceConfig {
    // 데이터 소스 설정
    @Bean(name = "onPremisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.on-premis.private")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    // EntityManagerFactory 설정
    @Bean(name = "onPremisEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("onPremisDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.example.onpremis.entity") // Entity 클래스의 위치를 지정
                .persistenceUnit("onPremis")
                .build();
    }

    // TransactionManager 설정
    @Bean(name = "onPremisTransactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("onPremisEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}

