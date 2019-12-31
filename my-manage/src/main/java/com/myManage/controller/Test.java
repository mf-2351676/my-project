package com.myManage.controller;

import com.netflix.zuul.context.RequestContext;
import com.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * @Author: menfeng
 * @Date: 2019/12/26 11:20
 * @Version 1.0
 */
@Slf4j
@RestController
public class Test {

    @GetMapping("/test")
    public String test(HttpServletRequest request){
        log.info("header = {}",request.getHeader("token"));
        String user = RedisUtils.getRedisUtil().get(request.getHeader("token"));
        log.info("user-------------{}",user);
        return "<a href='http://127.0.0.1:9084/logout'/>退出";
    }
}
