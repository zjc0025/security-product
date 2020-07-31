package com.zjc.social.controller;

import com.zjc.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName SocialController
 * @Description
 * @Author ZJC
 * @Date 2020/7/31 14:59
 */
@Controller
public class SocialController {

    @Autowired
    UserService userService;

    @GetMapping("/home/index/{userId}")
    public String index(@PathVariable String userId, Model model){
        model.addAttribute("timeline", userService.getTimeline(userId, "home:",0,100));
        return "home/index";
    }

}
