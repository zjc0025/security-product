package com.zjc.security.web.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName 自定义登陆成功执行
 * @Description
 * @Author ZJC
 * @Date 2020/4/29 16:47
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
//        SysUser user = new SysUser();
//        SecurityUser securityUser = ((SecurityUser) auth.getPrincipal());
//        user.setToken(securityUser.getCurrentUserInfo().getToken());
//        ResponseUtils.out(response, ApiResult.ok("登录成功!", user));

        response.sendRedirect("home/index");
    }
}
