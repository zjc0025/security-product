package com.zjc.oauth.client.config;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.
                // 禁用 CSRF 跨站伪造请求，便于测试
                        csrf().disable()
                // 验证所有请求
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                //允许访问首页
                .antMatchers("/","/login").permitAll()
                .and()
                // 设置登出URL为 /logout
                .logout().logoutUrl("/logout").permitAll()
                .logoutSuccessUrl("/");
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
