package com.zjc.security.web.controller;

import com.alibaba.excel.EasyExcel;
import com.zjc.security.web.dto.DemoData;
import com.zjc.security.web.utils.DemoDataListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    public String upload(MultipartFile file) throws IOException {
        //读取第一张sheet
//        EasyExcel.read(file.getInputStream(), DemoData.class, new DemoDataListener()).sheet().doRead();
        //读取全部sheet
        EasyExcel.read(file.getInputStream(), DemoData.class, new DemoDataListener()).doReadAll();
        return "success";
    }

}
