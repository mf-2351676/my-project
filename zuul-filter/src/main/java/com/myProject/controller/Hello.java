package com.myProject.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: menfeng
 * @Date: 2020/11/6 13:54
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/labsys")
@Api(tags = "测试2")
public class Hello {

    @ApiOperation(httpMethod = "POST", value = "测试2")
    @GetMapping("index")
    public String index(){
        log.info("123");
        return "访问成功";
    }


}
