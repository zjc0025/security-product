package com.zjc.security.web.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName EsService
 * @Description
 * @Author ZJC
 * @Date 2020/7/3 9:12
 */
@Slf4j
@Service
public class EsSearchService {

    @Autowired
    RestHighLevelClient client;


    public String searchRequest(String indexName) {
        String res = "";
        try{
            SearchRequest searchRequest = new SearchRequest("webtest");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            RestStatus status = searchResponse.status();
            TimeValue took = searchResponse.getTook();
            Boolean terminatedEarly = searchResponse.isTerminatedEarly();
            boolean timedOut = searchResponse.isTimedOut();

            SearchHits hits = searchResponse.getHits();
            TotalHits totalHits = hits.getTotalHits();
            // the total number of hits, must be interpreted in the context of totalHits.relation
            long numHits = totalHits.value;
            // whether the number of hits is accurate (EQUAL_TO) or a lower bound of the total (GREATER_THAN_OR_EQUAL_TO)
            TotalHits.Relation relation = totalHits.relation;
            float maxScore = hits.getMaxScore();

            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                // do something with the SearchHit
                String index = hit.getIndex();
                String id = hit.getId();
                float score = hit.getScore();

                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String documentTitle = (String) sourceAsMap.get("title");
//                List<Object> users = (List<Object>) sourceAsMap.get("user");
                Map<String, Object> innerObject =
                        (Map<String, Object>) sourceAsMap.get("innerObject");

                log.info(hit.getSourceAsString());
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
        return res;
    }

    public String searchHighLightRequest(String indexName) {
        StringBuilder res = new StringBuilder();
        try{
            SearchRequest searchRequest = new SearchRequest("webtest");

            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightTitle =
                    new HighlightBuilder.Field("title");
            highlightTitle.highlighterType("unified");
            highlightBuilder.field(highlightTitle);
            HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
            highlightBuilder.field(highlightUser);
            searchSourceBuilder.highlighter(highlightBuilder);

            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits.getHits()) {
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlight = highlightFields.get("title");
                Text[] fragments = highlight.fragments();
                String fragmentString = fragments[0].string();
                log.info(fragmentString);
                res.append(fragmentString);
            }

        }catch (Exception e){
            log.error(e.getMessage());
        }
        return res.toString();
    }

}
