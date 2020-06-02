package com.zjc.security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName TestController
 * @Description
 * @Author ZJC
 * @Date 2020/6/1 15:59
 */
@Controller
public class TestController {

    @GetMapping("/test/sub2")
    public String multiUpload(){
        return "test/sub2";
    }

    @ResponseBody
    @PostMapping("/miniui/upload")
    public String upload(MultipartFile[] files){
        System.out.println(files);
        return "success";
    }

}
