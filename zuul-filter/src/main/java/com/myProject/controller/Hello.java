package com.myProject.controller;

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
public class Hello {

    @GetMapping("index")
    public String index(){
        log.info("123");
        return "访问成功";
    }
}
