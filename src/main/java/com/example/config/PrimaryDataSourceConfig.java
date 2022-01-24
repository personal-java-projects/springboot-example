package com.example.config;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Stream;

@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
@MapperScan(basePackages = "com.example.mapper", sqlSessionFactoryRef = "PrimarySqlSessionFactory")//basePackages:接口文件的包路径
public class PrimaryDataSourceConfig {
    @Autowired
    private MybatisProperties properties;

    @Autowired
    private Environment env;

    @Bean(name = "PrimaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    @Primary
    public DataSource getPrimaryDateSource() throws SQLException {
        HikariDataSource dataSource = DataSourceBuilder.create().type(HikariDataSource.class).build();
        dataSource.setConnectionTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.connection-timeout")));
        dataSource.setValidationTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.validation-timeout")));
        dataSource.setIdleTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.idle-timeout")));
        dataSource.setLoginTimeout(Integer.parseInt(env.getProperty("spring.datasource.hikari.login-timeout")));
        dataSource.setMaxLifetime(Long.parseLong(env.getProperty("spring.datasource.hikari.max-lifetime")));
        dataSource.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size")));
        dataSource.setMinimumIdle(Integer.parseInt(env.getProperty("spring.datasource.hikari.minimum-idle")));
        dataSource.setReadOnly(Boolean.parseBoolean(env.getProperty("spring.datasource.hikari.read-only")));
        return dataSource;
    }

    @Bean(name = "PrimarySqlSessionFactory")
    @ConfigurationProperties(prefix = "mybatis")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("PrimaryDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        bean.setMapperLocations(resolveMapperLocations(properties.getMapperLocations()));
        bean.getObject().getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
//        properties.getConfiguration().setJdbcTypeForNull(JdbcType.NULL);
        bean.setConfiguration(properties.getConfiguration());
        return bean.getObject();
    }

    private Resource[] resolveMapperLocations(String[] mapperLocations) {
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

        return Stream.of(Optional.ofNullable(mapperLocations).orElse(new String[0]))
                .flatMap(location -> Stream.of(getResources(pathMatchingResourcePatternResolver, location))).toArray(Resource[]::new);
    }

    private Resource[] getResources(PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver, String location) {
        try {
            return pathMatchingResourcePatternResolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

    @Bean(name = "PrimaryTransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("PrimaryDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}