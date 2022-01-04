package com.example.config;

import com.zaxxer.hikari.HikariDataSource;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
@MapperScan(basePackages = "com.example.mapper", sqlSessionFactoryRef = "PrimarySqlSessionFactory")//basePackages:接口文件的包路径
public class PrimaryDataSourceConfig {
    @Autowired
    private MybatisProperties properties;

    @Bean(name = "PrimaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    @Primary
    public DataSource getPrimaryDateSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
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