package com.zjc.security.web.config;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

/**
 * @ClassName AuthInterceptor
 * @Description
 * @Author ZJC
 * @Date 2020/5/6 10:03
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private String defaultFailureUrl;

    private MySessionAuthenticationStrategy mySessionAuthenticationStrategy;

    public void setDefaultFailureUrl(String defaultFailureUrl) {
        this.defaultFailureUrl = defaultFailureUrl;
    }

    public void setSessionStrategy(MySessionAuthenticationStrategy mySessionAuthenticationStrategy) {
        this.mySessionAuthenticationStrategy = mySessionAuthenticationStrategy;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        //获取当前会话的session信息
        SessionRegistry sessionRegistry = mySessionAuthenticationStrategy.getSessionRegistry();
        SessionInformation sessionInformation = sessionRegistry.getSessionInformation(request.getSession().getId());
        if(sessionInformation.isExpired()){
            sessionRegistry.removeSessionInformation(sessionInformation.getSessionId());
            response.sendRedirect(defaultFailureUrl + "?msg="+ URLEncoder.encode("登录过期！","utf-8"));
            return false;
        }
        return true;
    }

}
