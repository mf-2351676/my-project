package com.myManage.controller;

import com.utils.RedisUtil;
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
public class Test {

    @GetMapping("/test")
    public String test() {
       /* String token = request.getHeader("token");
        log.info("test--token--:{}", token);
        String s = RedisUtils.getRedisUtil().get(token);
        log.info(s);
        String pwd = PasswdUtil.getPwd("1", "admin");
        return "admin的密码是：" + pwd + "<a href='http://127.0.0.1:9084/logout'/>退出";*/
       return "123456";
    }

    @GetMapping("/hello")
    public String testRedis(){
        log.info("why");
        RedisUtil.setString("123","helloworld");
        return RedisUtil.getString("123");
    }



}
