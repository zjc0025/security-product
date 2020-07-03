package com.zjc.security.web.controller;

import com.zjc.security.web.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName EsController
 * @Description
 * @Author ZJC
 * @Date 2020/7/3 9:08
 */
@Controller
public class EsController {

    @Autowired
    EsService esService;

    @ResponseBody
    @PutMapping("/es/createIndex")
    public String createIndex(String indexName){
        return esService.createIndex(indexName);
    }

}
