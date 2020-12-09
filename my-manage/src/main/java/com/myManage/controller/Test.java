package com.myManage.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: menfeng
 * @Date: 2019/12/26 11:20
 * @Version 1.0
 */
@Slf4j
@RestController
@Api(tags = "测试")
public class Test {

    @ApiOperation(httpMethod = "GET", value = "测试")
    @GetMapping("/test")
    public String test() {
       /* String token = request.getHeader("token");
        log.info("test--token--:{}", token);
        String s = RedisUtils.getRedisUtil().get(token);
        log.info(s);
        String pwd = PasswdUtil.getPwd("1", "admin");
        return "admin的密码是：" + pwd + "<a href='http://127.0.0.1:9084/logout'/>退出";*/
       return "192.168.21.59";
    }



}
