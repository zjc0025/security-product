package com.zjc.security.web.service;

import com.zjc.dao.mapper.SysUserMapper;
import com.zjc.dao.model.SecurityUser;
import com.zjc.dao.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserDetailsServiceImpl
 * @Description
 * @Author ZJC
 * @Date 2020/4/29 16:32
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /***
     * 根据账号获取用户信息
     * @param username:
     * @return: org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库中取出用户信息
//        SysUser sysUser = sysUserMapper.findByUsername(username);
        SysUser sysUser = sysUserMapper.findUserWithMenu(username);

        // 判断用户是否存在
        if (null == sysUser){
            throw new UsernameNotFoundException("用户名不存在！");
        }
        // 返回UserDetails实现类
        return new SecurityUser(sysUser);
    }
}
