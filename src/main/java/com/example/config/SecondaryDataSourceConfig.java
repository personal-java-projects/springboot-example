package com.example.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.example.mapper",sqlSessionFactoryRef = "SecondarySqlSessionFactory")
public class SecondaryDataSourceConfig {
    @Bean(name = "SecondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource getSecondaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "SecondarySqlSessionFactory")
    public SqlSessionFactory db2SqlSessionFactory(@Qualifier("SecondaryDataSource") DataSource datasource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(datasource);
        bean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mysql_mappers/*.xml"));
        return bean.getObject();
    }

    @Bean(name="SecondaryTransactionManager")
    public DataSourceTransactionManager transactionManagerOne(){
        return new DataSourceTransactionManager(getSecondaryDataSource());
    }

    @Bean(name = "SecondaryJdbcTemplate")
    public JdbcTemplate jdbcTemplate(
            @Qualifier("SecondaryDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
