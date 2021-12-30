package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.controller"))
                .paths(PathSelectors.any())
                .build()
                .consumes(newHashSet("application/json","application/xml", "multipart/form-data"))
                .protocols(newHashSet("text/plain; charset=utf-8", "text/html"))
                .protocols(newHashSet("https", "http"));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .description("knife4j在线API接口文档")
                .contact(new Contact("Roc-xb", null, null))
                .version("v3.0.0")
                .title("knife4j在线API接口文档")
                .build();
    }

    @SafeVarargs
    private final <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }
}
