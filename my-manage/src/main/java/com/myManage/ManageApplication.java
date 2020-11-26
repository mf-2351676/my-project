package com.myManage;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: menfeng
 * @Date: 2019/12/26 11:17
 * @Version 1.0
 */
@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@EnableFeignClients
@MapperScan({"com.myProject.dao.mapper.**"})
@Import(FdfsClientConfig.class)
//解决jmx注册冲突
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class ManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManageApplication.class);
    }
}
