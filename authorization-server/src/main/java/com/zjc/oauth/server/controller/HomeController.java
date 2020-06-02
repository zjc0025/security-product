package com.zjc.oauth.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName HomeController
 * @Description
 * @Author ZJC
 * @Date 2020/5/9 14:32
 */
@Controller
public class HomeController {

    @GetMapping("/login")
    public String index(){
        return "login";
    }

}
