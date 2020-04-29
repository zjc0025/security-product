package com.zjc.security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/timeout")
    public String timeout(){
        return "timeout";
    }

    @GetMapping("/loginFail")
    public String loginFail(Model model){
        model.addAttribute("errorMsg", "帐号或密码错误！");
        return "login";
    }

}
