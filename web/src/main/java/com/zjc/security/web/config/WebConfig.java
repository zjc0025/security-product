package com.zjc.security.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    MySessionAuthenticationStrategy mySessionAuthenticationStrategy;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/loginFail",
                        "/login",
                        "/css/**",
                        "/img/**",
                        "/js/**",
                        "/scss/**",
                        "/vendor/**",
                        "/**/*.png",
                        "/**/*.jpg"
                );
    }

    public AuthInterceptor authInterceptor() {
        AuthInterceptor authInterceptor = new AuthInterceptor();
        authInterceptor.setDefaultFailureUrl("/loginFail");//拦截器验证登录状态失败后默认跳转的url
        authInterceptor.setSessionStrategy(mySessionAuthenticationStrategy);
        return authInterceptor;
    }

}
