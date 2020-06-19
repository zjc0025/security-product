package com.zjc.oauth.resource.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserInfoController
 * @Description
 * @Author ZJC
 * @Date 2020/6/19 14:43
 */
@RestController
@RequestMapping("user")
public class UserInfoController {

    @GetMapping("/userInfo/{id}")
    public Map<String,String> meet(@PathVariable("id") String id){
        System.out.println("获取id为："+ id + "的用户信息！");
        Map<String,String> map = new HashMap();
        map.put("id","123");
        map.put("name","兰兰");
        map.put("age","18");
        map.put("sex","女");
        return map;
    }

}
