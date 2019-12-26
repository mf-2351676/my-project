package com.myProject.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bean.HttpResult;
import com.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collection;

/**
 * @Author: menfeng
 * @Date: 2019/10/23 17:41
 * @Version 1.0
 */
@Controller
@Slf4j
public class Hello {

    @GetMapping("/index")
    @ResponseBody
    public String sayHello() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Object details = authentication.getDetails();
        JSONObject json = (JSONObject) JSONObject.toJSON(details);
        String s = json.getString("tokenValue");
        String url = "http://192.168.138.101:8080/cas/oauth2.0/profile?access_token=" + s;
        try {
            System.out.println(123);
            HttpResult doGet = HttpClientUtils.doGet(url);
            log.info("doGet : {}", doGet);
        } catch (Exception e) {

        }
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "<a href='/logout'/>退出";
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
