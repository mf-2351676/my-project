package com.myProject.controller;

import com.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("upload")
    public Map<String,Object> upload(@RequestParam("file") MultipartFile file) {
        log.info("进入upload");
        Map<String, Object> data = new HashMap<>();
        if (file.isEmpty()) {
            data.put("code",404);
            data.put("message","文件为空");
            return data;
        }
        try {
            String path= FileUtils.saveFile(file);
            data.put("code",200);
            data.put("message","文件上传成功");
            data.put("fileName",file.getOriginalFilename());
            data.put("filePath",path);
        } catch (Exception e) {
            //log.info("upload file failed",e);
            System.out.println(e);
        }
        return data;
    }
}
