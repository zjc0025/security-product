package com.zjc.security.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * @ClassName WebSecurityConfig
 * @Description
 * @Author ZJC
 * @Date 2020/4/21 11:02
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource; // 数据源

    /**
     * 自定义用户密码校验过滤器
     */
    private final MyAuthenticationProcessingFilter myAuthenticationProcessingFilter;

    public WebSecurityConfig(MyAuthenticationProcessingFilter myAuthenticationProcessingFilter) {
        this.myAuthenticationProcessingFilter = myAuthenticationProcessingFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //设置用户登录单机的最大数和错误页面
//        http.sessionManagement().maximumSessions(1).expiredUrl("/timeout");

        http
                .authorizeRequests()
                .antMatchers("/es/**", "/register", "/loginFail", "/home", "/css/**", "/img/**", "/js/**", "/scss/**", "/vendor/**", "/**/*.png", "/**/*.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
//                .failureUrl("/loginFail")
                .permitAll()
                .and()
                .logout()
                .permitAll();
//                .and()
//                .rememberMe()
//                .userDetailsService(myUserDetailsService) // 设置userDetailsService
//                .tokenRepository(persistentTokenRepository()) // 设置数据访问层
//                .tokenValiditySeconds(24 * 60 * 60); // 记住我的时间(秒);

        //允许iframe
        http.headers().frameOptions().disable();

        // 自定义过滤器认证用户名密码
        http.addFilterAt(myAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);

        http.csrf().disable();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .passwordEncoder(new BCryptPasswordEncoder())
//                .withUser("user")
//                .password(new BCryptPasswordEncoder().encode("123456"))
//                .roles("USER");
//    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(myUserDetailsService)
//                .passwordEncoder(new BCryptPasswordEncoder());
//    }

    /**
     * 持久化token
     * <p>
     * Security中，默认是使用PersistentTokenRepository的子类InMemoryTokenRepositoryImpl，将token放在内存中
     * 如果使用JdbcTokenRepositoryImpl，会创建表persistent_logins，将token持久化到数据库
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource); // 设置数据源
//        tokenRepository.setCreateTableOnStartup(true); // 启动创建表，创建成功后注释掉
        return tokenRepository;
    }


}