package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@SpringBootApplication
public class SpringbootExampleApplication {

	public static void main(String[] args) {
		System.out.println("启动参数: " + args);
		SpringApplication.run(SpringbootExampleApplication.class, args);
	}

}
