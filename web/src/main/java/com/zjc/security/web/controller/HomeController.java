package com.zjc.security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName HelloController
 * @Description
 * @Author ZJC
 * @Date 2020/4/21 10:09
 */
@Controller
public class HomeController {

    @GetMapping("/home/index")
    public String index(){
        return "home/index";
    }

}
