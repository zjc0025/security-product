package com.zjc.security.web.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @ClassName 自定义认证管理器
 * @Description
 * @Author ZJC
 * @Date 2020/4/29 16:28
 */
@Component
public class MyAuthenticationManager implements AuthenticationManager {
    private final MyAuthenticationProvider myAuthenticationProvider;

    public MyAuthenticationManager(MyAuthenticationProvider myAuthenticationProvider) {
        this.myAuthenticationProvider = myAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //身份验证
        Authentication result = myAuthenticationProvider.authenticate(authentication);
        if(null == result){
            throw new ProviderNotFoundException("Authentication failed!");
        }
        return result;
    }
}
