package com.zjc.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @ClassName Application
 * @Description
 * @Author ZJC
 * @Date 2020/7/30
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }


}
