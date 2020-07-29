package com.zjc.security.web.controller;

import com.zjc.security.web.service.EsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName EsSearchController
 * @Description
 * @Author ZJC
 * @Date 2020/7/6 15:36
 */
@RestController
public class EsSearchController {

    @Autowired
    EsSearchService esSearchService;

    @GetMapping("/es/searchRequest")
    public String searchRequest(String indexName) {
        return esSearchService.searchRequest(indexName);
    }

    @GetMapping("/es/searchHighLightRequest")
    public String searchHighLightRequest(String indexName) {
        return esSearchService.searchHighLightRequest(indexName);
    }

}
