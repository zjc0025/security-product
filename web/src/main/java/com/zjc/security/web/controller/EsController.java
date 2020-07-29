package com.zjc.security.web.controller;

import com.zjc.security.web.service.EsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName EsController
 * @Description
 * @Author ZJC
 * @Date 2020/7/3 9:08
 */
@RestController
public class EsController {

    @Autowired
    EsService esService;

    @PutMapping("/es/createIndex")
    public String createIndex(String indexName) {
        return esService.createIndex(indexName);
    }

    @GetMapping("/es/getRequest")
    public String getRequest(String indexName) {
        return esService.getRequest(indexName);
    }

    @GetMapping("/es/getSourceRequest")
    public String getSourceRequest(String indexName) {
        return esService.getSourceRequest(indexName);
    }

    @GetMapping("/es/existsRequest")
    public String existsRequest(String indexName) {
        return esService.existsRequest(indexName);
    }

    @DeleteMapping("/es/deleteRequest")
    public String deleteRequest(String indexName) {
        return esService.deleteRequest(indexName);
    }

    @PutMapping("/es/updateRequest")
    public String updateRequest(String indexName) {
        return esService.updateRequest(indexName);
    }

    @GetMapping("/es/termVectorsRequest")
    public String termVectorsRequest(String indexName) {
        return esService.termVectorsRequest(indexName);
    }

    @PostMapping("/es/bulkRequest")
    public String bulkRequest(String indexName) {
        return esService.bulkRequest(indexName);
    }

    @GetMapping("/es/multiGetRequest")
    public String multiGetRequest(String indexName) {
        return esService.multiGetRequest(indexName);
    }

    @GetMapping("/es/reindexRequest")
    public String reindexRequest(String indexName) {
        return esService.reindexRequest(indexName);
    }

    @PostMapping("/es/updateByQueryRequest")
    public String updateByQueryRequest(String indexName) {
        return esService.updateByQueryRequest(indexName);
    }

    @DeleteMapping("/es/deleteByQueryRequest")
    public String deleteByQueryRequest(String indexName) {
        return esService.deleteByQueryRequest(indexName);
    }

    @PostMapping("/es/rethrottleRequest")
    public String rethrottleRequest(String indexName) {
        return esService.rethrottleRequest(indexName);
    }

    @PostMapping("/es/multiTermVectorsRequest")
    public String multiTermVectorsRequest(String indexName) {
        return esService.multiTermVectorsRequest(indexName);
    }

}
