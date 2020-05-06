package com.zjc.security.web.config;

import com.zjc.security.web.service.MyUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * @ClassName 自定义用户密码校验过滤器
 * @Description
 * @Author ZJC
 * @Date 2020/4/29 16:09
 */
@Slf4j
@Component
public class MyAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    MySessionAuthenticationStrategy mySessionAuthenticationStrategy;

    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";

    /**
     * @param authenticationManager:             认证管理器
     * @param myAuthenticationSuccessHandler: 认证成功处理
     * @param myAuthenticationFailureHandler: 认证失败处理
     */
    public MyAuthenticationProcessingFilter(MyAuthenticationManager authenticationManager,
                                            MyAuthenticationSuccessHandler myAuthenticationSuccessHandler,
                                            MyAuthenticationFailureHandler myAuthenticationFailureHandler,
                                            MySessionAuthenticationStrategy mySessionAuthenticationStrategy
                                            ) {
        super(new AntPathRequestMatcher("/login", "POST"));
        this.setAuthenticationManager(authenticationManager);
        this.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        this.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        //开启一个账号用户登录数限制
        this.setSessionAuthenticationStrategy(mySessionAuthenticationStrategy);
        //开启一个记住我的实现
//        this.setRememberMeServices(new PersistentTokenBasedRememberMeServices("myRememberMe",myUserDetailsService,persistentTokenRepository()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        if (request.getContentType() == null || !request.getContentType().contains("application/json")) {
//            throw new AuthenticationServiceException("请求头类型不支持: " + request.getContentType());
//        }

        String username = request.getParameter(USERNAME_PARAMETER);
        String password = request.getParameter(PASSWORD_PARAMETER);

        //登录必须是post请求
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        UsernamePasswordAuthenticationToken authRequest;
        try {
//            MultiReadHttpServletRequest wrappedRequest = new MultiReadHttpServletRequest(request);
            // 将前端传递的数据转换成jsonBean数据格式
//            User user = JSONObject.parseObject(wrappedRequest.getBodyJsonStrByJson(wrappedRequest), User.class);
            authRequest = new UsernamePasswordAuthenticationToken(username, password);
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage());
        }

//        SysUser sysUser = new SysUser();
//        sysUser.setUsername(username);
//        //获取当前用户已登录的session信息
//        SessionRegistry sessionRegistry = mySessionAuthenticationStrategy.getSessionRegistry();
//        List<SessionInformation> infos = sessionRegistry.getAllSessions(new SecurityUser(sysUser),true);
//        for(SessionInformation info : infos){
//            if(info.getSessionId().equals(request.getSession().getId()) && info.isExpired()){
//                sessionRegistry.removeSessionInformation(info.getSessionId());
//                throw new AuthenticationServiceException("登陆过期");
//            }
//        }

        return this.getAuthenticationManager().authenticate(authRequest);
    }

//    @Bean
//    public PersistentTokenRepository persistentTokenRepository() {
//        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
//        tokenRepository.setDataSource(dataSource); // 设置数据源
////        tokenRepository.setCreateTableOnStartup(true); // 启动创建表，创建成功后注释掉
//        return tokenRepository;
//    }
}
