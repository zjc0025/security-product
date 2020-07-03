package com.zjc.security.web.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.GetSourceRequest;
import org.elasticsearch.client.core.GetSourceResponse;
import org.elasticsearch.client.core.TermVectorsRequest;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.reindex.ReindexRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

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

    /**
     * @param indexName
     * @return java.lang.String
     * @Description 创建索引
     **/
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

    /**
     * @param indexName
     * @return java.lang.String
     * @Description get api
     **/
    public String getRequest(String indexName) {
        try {
            GetRequest request = new GetRequest(indexName, "1");
            //禁用源检索，默认情况下启用(_source)
//            request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
            //设置_source中包含的字段
            String[] includes = new String[]{"message", "*Date"}; //包含字段
            String[] excludes = Strings.EMPTY_ARRAY;  //不包含字段
            FetchSourceContext fetchSourceContext =
                    new FetchSourceContext(true, includes, excludes);
            request.fetchSourceContext(fetchSourceContext);

            //设置_source中不包含的字段
//        String[] includes = Strings.EMPTY_ARRAY;
//        String[] excludes = new String[]{"message"};
//        FetchSourceContext fetchSourceContext =
//                new FetchSourceContext(true, includes, excludes);
//        request.fetchSourceContext(fetchSourceContext);

            //配置特定存储字段的检索
            request.storedFields("message");
            GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);
//            String message = getResponse.getField("message").getValue();
//            log.info(message);
            return getResponse.getSourceAsString();
        } catch (Exception e) {
            log.error(e.getMessage());
            return e.getMessage();
        }

    }

    /**
     * @param indexName
     * @return java.lang.String
     * @Description get source api
     **/
    public String getSourceRequest(String indexName) {
        String res = "";
        try {
            GetSourceRequest getSourceRequest = new GetSourceRequest(indexName, "1");
            //设置路由
//        getSourceRequest.routing("routing");
            //设置偏好
//        getSourceRequest.preference("preference");
            //将实时标志设置为false（默认为true）
            getSourceRequest.realtime(false);
            //检索文档之前执行刷新（默认为false）
            getSourceRequest.refresh(true);

            GetSourceResponse response =
                    client.getSource(getSourceRequest, RequestOptions.DEFAULT);

            res = response.getSource().toString();

        } catch (Exception e) {
            log.error(e.getMessage());
            res = e.getMessage();
        }
        return res;

    }

    /**
     * @param indexName
     * @return java.lang.String
     * @Description 当store为false时(默认配置 ） ， 这些field只存储在 " _source " field中 。
     *当store为true时 ， 这些field的value会存储在一个跟_source平级的独立的field中 。 同时也会存储在_source中 ， 所以有两份拷贝 。
     **/
    public String existsRequest(String indexName) {
        String res = "";
        try {
            GetRequest getRequest = new GetRequest(indexName, "1");
            //禁用抓取_source数据
            getRequest.fetchSourceContext(new FetchSourceContext(false));
            //禁用获取存储的字段
            getRequest.storedFields("_none_");

            boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);

            res = String.valueOf(exists);

        } catch (Exception e) {
            log.error(e.getMessage());
            res = e.getMessage();
        }
        return res;

    }

    public String deleteRequest(String indexName) {
        String res = "";
        try {
            DeleteRequest request = new DeleteRequest(indexName, "1");

            request.routing("routing");

            request.timeout(TimeValue.timeValueMinutes(2));
            request.timeout("2m");

            request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
            request.setRefreshPolicy("wait_for");

//            request.version(2);
//            request.versionType(VersionType.EXTERNAL);

            DeleteResponse deleteResponse = client.delete(
                    request, RequestOptions.DEFAULT);

            ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
            if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

            }
            if (shardInfo.getFailed() > 0) {
                for (ReplicationResponse.ShardInfo.Failure failure :
                        shardInfo.getFailures()) {
                    String reason = failure.reason();
                    log.info("失败信息：" + reason);
                }
            }

            res = deleteResponse.getResult().toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            res = e.getMessage();
        }
        return res;
    }

    public String updateRequest(String indexName) {
        String res = "";
        try {
            UpdateRequest request = new UpdateRequest(indexName, "1");

            //用map存放参数
            Map<String, Object> parameters = singletonMap("count", 4);
            //创建一个脚本 功能为 field字段值加4
//            Script inline = new Script(ScriptType.INLINE, "painless",
//                    "ctx._source.field += params.count", parameters);
//            request.script(inline);

            //或者是一个存储的脚本
//        Script stored = new Script(
//                ScriptType.STORED, null, "increment-field", parameters);
//        request.script(stored);

            //修改部分文档
            //方式1---------------------------------------------------------------------
            String jsonString = "{" +
                    "\"updated\":\"2017-01-01\"," +
                    "\"reason\":\"daily update\"" +
                    "}";
            request.doc(jsonString, XContentType.JSON);
            //方式2---------------------------------------------------------------------
//        Map<String, Object> jsonMap = new HashMap<>();
//        jsonMap.put("updated", new Date());
//        jsonMap.put("reason", "daily update");
//        UpdateRequest request = new UpdateRequest("posts", "1")
//                .doc(jsonMap);
            //方式3---------------------------------------------------------------------
//        XContentBuilder builder = XContentFactory.jsonBuilder();
//        builder.startObject();
//        {
//            builder.timeField("updated", new Date());
//            builder.field("reason", "daily update");
//        }
//        builder.endObject();
//        UpdateRequest request = new UpdateRequest("posts", "1")
//                .doc(builder);
            //方式4---------------------------------------------------------------------
//        UpdateRequest request = new UpdateRequest("posts", "1")
//                .doc("updated", new Date(),
//                        "reason", "daily update");

            //不存在就添加
//        String jsonString = "{\"created\":\"2017-01-01\"}";
//        request.upsert(jsonString, XContentType.JSON);

            //如冲突重试3次
//            request.retryOnConflict(3);

            //指定版本
//            request.setIfSeqNo(2L);
//            request.setIfPrimaryTerm(1L);

            //禁用此行为后，不更改任何内容的更新也会返回updated并且文档版本号加1；
            //不禁用此行为，不更改任何内容的更新会返回noop并且文档版本号不变。
//            request.detectNoop(false);

            //设置脚本不管文档是否存在都执行
//            request.scriptedUpsert(true);
            //指示部分文档（如果尚不存在）必须用作upsert文档。
            request.docAsUpsert(true);

            //设置更新前确认存活的分片数
//        request.waitForActiveShards(2);
//        request.waitForActiveShards(ActiveShardCount.ALL);

            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);

            res = updateResponse.getResult().toString();


        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    public String termVectorsRequest(String indexName) {
        String res = "";
        try {
//            TermVectorsRequest request = new TermVectorsRequest("authors", "1");
//            request.setFields("user");

            XContentBuilder docBuilder = XContentFactory.jsonBuilder();
            docBuilder.startObject().field("user", "guest-user").endObject();
            TermVectorsRequest request = new TermVectorsRequest("authors",
                    docBuilder);

            //省略文档计数，文档频率之和，总术语频率之和
            request.setFieldStatistics(false);
            //显示总术语频率和文档频率
            request.setTermStatistics(true);
            //省略位置的输出
            request.setPositions(false);
            //省略偏移量的输出
            request.setOffsets(false);
            //省略有效载荷的输出
            request.setPayloads(false);

            Map<String, Integer> filterSettings = new HashMap<>();
            filterSettings.put("max_num_terms", 3);
            filterSettings.put("min_term_freq", 1);
            filterSettings.put("max_term_freq", 10);
            filterSettings.put("min_doc_freq", 1);
            filterSettings.put("max_doc_freq", 100);
            filterSettings.put("min_word_length", 1);
            filterSettings.put("max_word_length", 10);

            request.setFilterSettings(filterSettings);

            Map<String, String> perFieldAnalyzer = new HashMap<>();
            //指定与该字段不同的分析器
            perFieldAnalyzer.put("user", "keyword");
            request.setPerFieldAnalyzer(perFieldAnalyzer);

            TermVectorsResponse response =
                    client.termvectors(request, RequestOptions.DEFAULT);

            res = response.getTermVectorsList().toString();

            for (TermVectorsResponse.TermVector tv : response.getTermVectorsList()) {
                String fieldname = tv.getFieldName();
                int docCount = tv.getFieldStatistics().getDocCount();
                long sumTotalTermFreq =
                        tv.getFieldStatistics().getSumTotalTermFreq();
                long sumDocFreq = tv.getFieldStatistics().getSumDocFreq();
                if (tv.getTerms() != null) {
                    List<TermVectorsResponse.TermVector.Term> terms =
                            tv.getTerms();
                    for (TermVectorsResponse.TermVector.Term term : terms) {
                        String termStr = term.getTerm();
                        int termFreq = term.getTermFreq();
                        int docFreq = term.getDocFreq();
                        long totalTermFreq = term.getTotalTermFreq();
                        float score = term.getScore();
                        if (term.getTokens() != null) {
                            List<TermVectorsResponse.TermVector.Token> tokens =
                                    term.getTokens();
                            for (TermVectorsResponse.TermVector.Token token : tokens) {
                                int position = token.getPosition();
                                int startOffset = token.getStartOffset();
                                int endOffset = token.getEndOffset();
                                String payload = token.getPayload();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    public String bulkRequest(String indexName) {
        String res = "";
        try {
            BulkRequest request = new BulkRequest();
            request.add(new DeleteRequest("posts", "3"));
            request.add(new UpdateRequest("posts", "2")
                    .doc(XContentType.JSON, "other", "test"));
            request.add(new IndexRequest("posts").id("4")
                    .source(XContentType.JSON, "field", "baz"));

            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);

            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                DocWriteResponse itemResponse = bulkItemResponse.getResponse();

                switch (bulkItemResponse.getOpType()) {
                    case INDEX:
                    case CREATE:
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        break;
                    case UPDATE:
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        break;
                    case DELETE:
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                }
            }
            res = bulkResponse.getItems().toString();
        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    public String multiGetRequest(String indexName) {
        String res = "";
        try {
            MultiGetRequest request = new MultiGetRequest();
            request.add(new MultiGetRequest.Item("index", "example_id"));
            request.add(new MultiGetRequest.Item("index", "another_id"));

            MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
            MultiGetItemResponse item = response.getResponses()[0];


            res = item.toString();

            //遍历
            MultiGetItemResponse firstItem = response.getResponses()[0];
            Assert.isNull(firstItem);
            GetResponse firstGet = firstItem.getResponse();
            String index = firstItem.getIndex();
            String id = firstItem.getId();
            if (firstGet.isExists()) {
                long version = firstGet.getVersion();
                String sourceAsString = firstGet.getSourceAsString();
                Map<String, Object> sourceAsMap = firstGet.getSourceAsMap();
                byte[] sourceAsBytes = firstGet.getSourceAsBytes();
            } else {

            }

            //获取异常信息
            Exception e = item.getFailure().getFailure();
            ElasticsearchException ee = (ElasticsearchException) e;


        } catch (Exception e) {
            res = e.getMessage();
        }
        return res;
    }

    public String reindexRequest(String indexName) {
        String res = "";
        try{
            ReindexRequest request = new ReindexRequest();
            //源索引
            request.setSourceIndices("source1", "source2");
            //目标索引
            request.setDestIndex("dest");
            //设置目标索引版本号类型为外部
            request.setDestVersionType(VersionType.EXTERNAL);


        }catch (Exception e){

        }
        return res;
    }
}
