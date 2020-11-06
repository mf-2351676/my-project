package com.myProject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: menfeng
 * @Date: 2020/11/6 13:54
 * @Version 1.0
 */
@RestController
@RequestMapping("/labsys")
public class Hello {

    @GetMapping("index")
    public String index(){
        return "访问成功";
    }
}
