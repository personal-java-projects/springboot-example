package com.example.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
//扫描mapper包,mapper接口,将mapper接口注册到spring容器中
@MapperScan("com.example.mapper")
public class MybatisConfig {

}
