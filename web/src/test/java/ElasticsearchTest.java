import com.alibaba.fastjson.JSON;
import com.zjc.security.web.vo.ElasticTestVo;
import org.apache.http.HttpHost;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;

import java.io.IOException;

/**
 * @ClassName ElasticsearchTest
 * @Description
 * @Author ZJC
 * @Date 2020/6/3 15:39
 */
public class ElasticsearchTest {

    @Test
    public void test() throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.204.66", 9200, "http")));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("sex", "男");
        sourceBuilder.query(matchQueryBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("test_oracle_one");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits =  searchResponse.getHits();
        for(SearchHit hit:searchHits.getHits()){
//            System.out.println( hit.getSourceAsString());
            ElasticTestVo vo = JSON.parseObject(hit.getSourceAsString(), ElasticTestVo.class);
            System.out.println(vo);
        }
    }

}
