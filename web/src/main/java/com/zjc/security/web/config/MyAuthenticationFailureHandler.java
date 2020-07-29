package com.zjc.security.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @ClassName 自定义登录失败
 * @Description
 * @Author ZJC
 * @Date 2020/4/29 16:49
 */
@Slf4j
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
//        ApiResult result;
//        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
//            result = ApiResult.fail(e.getMessage());
//        } else if (e instanceof LockedException) {
//            result = ApiResult.fail("账户被锁定，请联系管理员!");
//        } else if (e instanceof CredentialsExpiredException) {
//            result = ApiResult.fail("证书过期，请联系管理员!");
//        } else if (e instanceof AccountExpiredException) {
//            result = ApiResult.fail("账户过期，请联系管理员!");
//        } else if (e instanceof DisabledException) {
//            result = ApiResult.fail("账户被禁用，请联系管理员!");
//        } else {
//            log.error("登录失败：", e);
//            result = ApiResult.fail("登录失败!");
//        }
//        ResponseUtils.out(response, result);

        response.sendRedirect("/loginFail?msg=" + URLEncoder.encode(e.getMessage(), "utf-8"));
    }

}
