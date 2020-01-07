package com.myProject.controller;


import com.alibaba.fastjson.JSONObject;
import com.bean.HttpResult;
import com.utils.HttpClientUtils;
import com.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * @Author: menfeng
 * @Date: 2019/10/23 17:41
 * @Version 1.0
 */
@Controller
@Slf4j
public class Hello {

    @GetMapping("/index")
    public String sayHello(Principal principal) {
        try {
            log.info("principal ------------ {}",principal);
            JSONObject pri = (JSONObject) JSONObject.toJSON(principal);
            JSONObject details = (JSONObject) JSONObject.toJSON(pri.get("details"));
            String token = details.getString("tokenValue");
            String userId = principal.getName();
            HttpResult doGet = HttpClientUtils.doGet("http://192.168.138.101:8080/cas/oauth2.0/profile?access_token=" + token);
            RedisUtils.getRedisUtil().set(token, doGet.getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:http://127.0.0.1:9084/manage/test";
    }

    @GetMapping("/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("退出");
            new SecurityContextLogoutHandler().logout(request, null, null);
            response.sendRedirect("http://192.168.138.101:8080/cas/logout?service=http://127.0.0.1:9084/index");
        } catch (Exception e) {
            log.info("退出失败");
        }
    }
}
