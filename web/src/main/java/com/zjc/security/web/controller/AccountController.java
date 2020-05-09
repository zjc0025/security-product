package com.zjc.security.web.controller;

import com.zjc.dao.model.SysUser;
import com.zjc.security.web.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName AccountController
 * @Description
 * @Author ZJC
 * @Date 2020/5/7 16:21
 */
@Controller
public class AccountController {

    @Autowired
    AccountService accountService;

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @PostMapping("/registerAccount")
    public void registerAccount(SysUser sysUser){
        accountService.registerAccount(sysUser);
    }

}
