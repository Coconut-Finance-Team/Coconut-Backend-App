package com.coconut.stock_app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.coconut.stock_app.repository.cloud",  // On-Premis Repository 위치
        entityManagerFactoryRef = "cloudEntityManager",
        transactionManagerRef = "cloudTransactionManager"
)
public class CloudDataSourceConfig {

    // EntityManagerFactory 설정
    @Bean
    public LocalContainerEntityManagerFactoryBean cloudEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cloudDatabaseDataSource());
        em.setPackagesToScan(new String[] {"com.coconut.stock_app.entity.cloud"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> prop = new HashMap<>();
        prop.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        prop.put("hibernate.hbm2ddl.auto", "update");
        prop.put("hibernate.format_sql", true);
        em.setJpaPropertyMap(prop);

        return em;
    }

    // 데이터 소스 설정
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.cloud")
    public DataSource cloudDatabaseDataSource() {
        return DataSourceBuilder.create().build();
    }


    // TransactionManager 설정
    @Bean
    public PlatformTransactionManager cloudTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(cloudEntityManager().getObject());
        return transactionManager;
    }
}

