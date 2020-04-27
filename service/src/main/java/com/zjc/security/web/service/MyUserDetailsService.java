package com.zjc.security.web.service;

import com.zjc.dao.mapper.SysUserMapper;
import com.zjc.dao.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @ClassName 自定义用户验证规则
 * @Description
 * @Author ZJC
 * @Date 2020/4/21 14:17
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    SysUserMapper userMapper;

    /**
     * @param [username]
     * @return org.springframework.security.core.userdetails.UserDetails
     * @author ZJC
     * @Description 根据数据库用户表验证帐号密码
     * @Date 2020/4/21 14:19
     **/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser user = userMapper.findByUsername(username);

        return new User(
                username,
                user.getPassword(),
                user.getEnabled(),
                user.getAccountnonlocked(),
                user.getCredentialsnonexpired(),
                user.getAccountnonlocked(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles())
        );
//        return new User(
//                username,
//                "111",
//                true,
//                true,
//                true,
//                true,
//                AuthorityUtils.commaSeparatedStringToAuthorityList("USER")
//        );
    }


}
