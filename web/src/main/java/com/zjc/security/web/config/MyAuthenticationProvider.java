package com.zjc.security.web.config;

import com.zjc.dao.model.SecurityUser;
import com.zjc.security.web.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @ClassName 自定义认证处理
 * @Description
 * @Author ZJC
 * @Date 2020/4/29 16:29
 */
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取前端表单中输入后返回的用户名、密码
        String userName = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        //采用SHA-256 +随机盐+密钥对密码进行加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        //根据用户名从数据库查询
        SecurityUser userInfo = (SecurityUser) userDetailsService.loadUserByUsername(userName);

        //输入的密码和数据库加密后的密码是否一致
//        boolean isValid = PasswordUtils.isValidPassword(password, userInfo.getPassword(), null);
        boolean isValid = bCryptPasswordEncoder.matches(password, userInfo.getPassword());
        // 验证密码是否一致
        if (!isValid) {
            throw new BadCredentialsException("密码错误！");
        }

        // 前后端分离情况下 处理逻辑...
        // 更新登录令牌 - 之后访问系统其它接口直接通过token认证用户权限...
//        String token = PasswordUtils.encodePassword(System.currentTimeMillis() + "1", "1");
//        SysUser user = userMapper.selectByPrimaryKey(userInfo.getCurrentUserInfo().getId());
//        user.setToken(token);
//        userMapper.updateById(user);
//        userInfo.getCurrentUserInfo().setToken(token);
        return new UsernamePasswordAuthenticationToken(userInfo, password, userInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
