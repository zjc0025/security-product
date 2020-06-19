package com.zjc.oauth.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;

/**
 * @ClassName MyAuthorizationServerConfig
 * @Description
 * @Author ZJC
 * @Date 2020/5/9 13:16
 */
@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
        security.tokenKeyAccess("isAuthenticated()");
        //允许表单认证
        security.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //这个地方指的是从jdbc查出数据来存储
        clients.withClientDetails(jdbcClientDetailsService());
    }

    @Bean
    public JdbcClientDetailsService  jdbcClientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 最后一个参数为替换之后页面的url
        endpoints.pathMapping("/oauth/confirm_access","/custom/confirm_access");
    }

//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.accessTokenConverter(jwtAccessTokenConverter());
//        endpoints.tokenStore(jwtTokenStore());
////        endpoints.tokenServices(defaultTokenServices());
//    }

    /*@Primary
    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(jwtTokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }*/

//    @Bean
//    public JwtTokenStore jwtTokenStore() {
//        return new JwtTokenStore(jwtAccessTokenConverter());
//    }
//
//    @Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//        jwtAccessTokenConverter.setSigningKey("zjc");   //  Sets the JWT signing key
//        return jwtAccessTokenConverter;
//    }


}
