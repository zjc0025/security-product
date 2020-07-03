package com.zjc.security.web.client;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ES客户端
 * @Description
 * @Author ZJC
 * @Date 2020/7/3 8:52
 */
@Configuration
public class EsClient {

    @Value("${elasticsearch.listOfServer}")
    String listOfServer;

    @Value("${elasticsearch.schema}")
    String schema;

    @Bean
    public RestHighLevelClient client() {
        List<HttpHost> httpHosts = new ArrayList<>();
        String[] addrs = listOfServer.split(",");
        for (String addr : addrs) {
            String ip = addr.split(":")[0];
            String port = addr.split(":")[1];
            httpHosts.add(new HttpHost(ip, Integer.parseInt(port), schema));
        }
        return new RestHighLevelClient(
                RestClient.builder(httpHosts.toArray(new HttpHost[]{})));
    }

}
