package com.zjc.dao.mapper;

import com.zjc.dao.model.SysUserRole;

public interface SysUserRoleMapper {
    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);
}