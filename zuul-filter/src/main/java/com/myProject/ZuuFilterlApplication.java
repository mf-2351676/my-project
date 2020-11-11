package com.myProject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@EnableFeignClients
@EnableZuulProxy
@MapperScan({"com.myProject.dao.mapper.**"})
public class ZuuFilterlApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuuFilterlApplication.class);
    }
}
