package com.coconut.stock_app.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.coconut.stock_app.repository.on_premise",  // On-Premise Repository 위치
        entityManagerFactoryRef = "onPremiseEntityManagerFactory",
        transactionManagerRef = "onPremiseTransactionManager"
)
public class OnPremiseDataSourceConfig {

    // On-Premise Master DataSource 설정
    @Primary
    @Bean(name = "onPremiseMasterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.on-premise.master")
    public DataSource onPremiseMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    // On-Premise Read Replica DataSource 설정
    @Bean(name = "onPremiseReadReplicaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.on-premise.read-replica")
    public DataSource onPremiseReadReplicaDataSource() {
        return DataSourceBuilder.create().build();
    }

    // EntityManagerFactory 설정
    @Primary
    @Bean(name = "onPremiseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean onPremiseEntityManagerFactory(
            @Qualifier("onPremiseMasterDataSource") DataSource masterDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(masterDataSource);
        em.setPackagesToScan("com.coconut.stock_app.entity.on_premise", "com.coconut.stock_app.entity.common");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true); // SQL 로그 출력
        vendorAdapter.setGenerateDdl(true); // DDL 자동 생성
        em.setJpaVendorAdapter(vendorAdapter);

        // Hibernate 추가 속성
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "update"); // 자동 테이블 업데이트
        properties.put("hibernate.format_sql", true); // SQL 포맷팅
        em.setJpaPropertyMap(properties);

        return em;
    }

    // TransactionManager 설정
    @Primary
    @Bean(name = "onPremiseTransactionManager")
    public PlatformTransactionManager onPremiseTransactionManager(
            @Qualifier("onPremiseEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}
