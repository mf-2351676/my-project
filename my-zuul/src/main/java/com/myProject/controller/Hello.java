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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Collection;

/**
 * @Author: menfeng
 * @Date: 2019/10/23 17:41
 * @Version 1.0
 */
@RestController
@Slf4j
public class Hello {

    @GetMapping("/index")
    public String sayHello(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Object details = authentication.getDetails();
        JSONObject json = (JSONObject) JSONObject.toJSON(details);
        String s = json.getString("tokenValue");
        String url = "http://192.168.138.101:8080/cas/oauth2.0/profile?access_token="+s;
        try{
            System.out.println(123);
            HttpResult doGet = HttpClientUtils.doGet(url);
            log.info("doGet : {}",doGet);
        }catch (Exception e){

        }
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "redirect:http://www.baidu.com";
    }
@GetMapping("/test")
    public void test(Principal principal){
        JSONObject json = (JSONObject) JSONObject.toJSON(principal);
    Object tokenValue = json.get("tokenValue");
    Object details = json.get("details");
    System.out.println(json);
    }
}
