package com.zjc.security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

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

    @GetMapping("/timeout")
    public String timeout(){
        return "timeout";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/loginFail")
    public String loginFail(String msg, Model model) throws UnsupportedEncodingException {
        if(!StringUtils.isEmpty(msg)){
            model.addAttribute("errorMsg", URLDecoder.decode(msg,"utf-8"));
        }
        return "login";
    }

}
