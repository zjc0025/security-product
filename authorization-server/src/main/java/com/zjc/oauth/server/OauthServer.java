package com.zjc.oauth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @ClassName Application
 * @Description
 * @Author ZJC
 * @Date 2020/5/9 13:52
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class OauthServer {

    public static void main(String[] args) {
        SpringApplication.run(OauthServer.class, args);
    }

}
