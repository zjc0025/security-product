package com.zjc.security.web.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName EsService
 * @Description
 * @Author ZJC
 * @Date 2020/7/3 9:12
 */
@Slf4j
@Service
public class EsService {

    @Autowired
    RestHighLevelClient client;

    public String createIndex(String indexName) {
        IndexResponse indexResponse = null;

        try {
            //请求构建方式1---------------------------------------------------------------------
            IndexRequest request = new IndexRequest(indexName);
            request.id("1");
            String jsonString = "{" +
                    "\"user\":\"kimchy\"," +
                    "\"postDate\":\"2013-01-30\"," +
                    "\"message\":\"trying out Elasticsearch\"" +
                    "}";
            request.source(jsonString, XContentType.JSON);

            //请求构建方式2---------------------------------------------------------------------
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("user", "kimchy");
            jsonMap.put("postDate", new Date());
            jsonMap.put("message", "trying out Elasticsearch");
            IndexRequest indexRequest2 = new IndexRequest(indexName).id("1").source(jsonMap);

            //请求构建方式3---------------------------------------------------------------------
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("user", "kimchy");
                builder.timeField("postDate", new Date());
                builder.field("message", "trying out Elasticsearch");
            }
            builder.endObject();
            IndexRequest indexRequest3 = new IndexRequest(indexName).id("1").source(builder);

            //请求构建方式4---------------------------------------------------------------------
            IndexRequest indexRequest4 = new IndexRequest("posts")
                    .id("1")
                    .source("user", "kimchy",
                            "postDate", new Date(),
                            "message", "trying out Elasticsearch");

            //请求配置项---------------------------------------------------------------------
            //1. routing值来限制只搜索那些分片而不是搜索index里的全部分片
//            request.routing("routing");
            //2. 设置超时时间
//            request.timeout(TimeValue.timeValueSeconds(1));  //方式1
            request.timeout("10s");  //方式2
            //3. 设置刷新时间
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);//方式1
            request.setRefreshPolicy("wait_for");//方式2
            //4. 设置版本号
//            request.version(2);
            //5. 设置外部版本号
//            request.versionType(VersionType.EXTERNAL);
            //6. 设置请求类型(若类型为create，如已存在相同id的文档，则报错)
            request.opType(DocWriteRequest.OpType.CREATE);//方式1
            request.opType("create");//方式2
            //7. 设置管道（相当于预处理，有点复杂）
//            request.setPipeline("pipeline");


            //获取响应（同步）
            indexResponse = client.index(request, RequestOptions.DEFAULT);


            //获取响应（异步）需要一个监听器
//            ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
//                @Override
//                public void onResponse(IndexResponse indexResponse) {
//                    //成功时执行
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    //失败时执行
//                }
//            };
//            IndexResponse indexResponse = client.indexAsync(request, RequestOptions.DEFAULT, listener);

            //请求结果分析

            //是否是第一次创建此文档
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {

            }
            //该文档是否是更新操作
            else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {

            }
            ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
            //是否所有分片都成功
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

            }
            //失败分片的原因
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure :
                        shardInfo.getFailures()) {
                    String reason = failure.reason();
                    log.info(reason);
                }
            }

            //打印请求结果
            log.info(indexResponse.toString());

            return indexResponse.getResult().toString();

        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }
    }

}
