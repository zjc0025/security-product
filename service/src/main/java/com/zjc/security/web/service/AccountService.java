package com.zjc.security.web.service;

import com.zjc.dao.mapper.SysUserMapper;
import com.zjc.dao.model.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @ClassName ProductService
 * @Description
 * @Author ZJC
 * @Date 2020/4/23 9:14
 */
@Service
public class AccountService {

    @Autowired
    SysUserMapper sysUserMapper;

    public void registerAccount(SysUser sysUser) {
        String password = sysUser.getPassword();
        password = new BCryptPasswordEncoder().encode(password);
        sysUser.setPassword(password);
        sysUserMapper.insert(sysUser);
    }

}
