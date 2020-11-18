package com.myManage.controller;

import com.alibaba.fastjson.JSON;
import com.utils.FileUtils;
import com.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

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

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        log.info("进入upload");
        long st = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        try {
            if (file.isEmpty()) {
                data.put("code", 404);
                data.put("message", "文件为空");
                return data;
            }
            String path = FileUtils.saveFile(file);
            data.put("code", 200);
            data.put("message", "文件上传成功");
            data.put("fileName", file.getOriginalFilename());
            data.put("filePath", path);
        } catch (Exception e) {
            log.info("upload file failed", e);
            data.put("code", 500);
            data.put("message", "上传失败");
            return data;
        }
        long nt = System.currentTimeMillis();
        log.info(JSON.toJSONString(data));
        log.info("上传时间为---{}", (nt - st) / 1000 + "s");
        return data;
    }

}
