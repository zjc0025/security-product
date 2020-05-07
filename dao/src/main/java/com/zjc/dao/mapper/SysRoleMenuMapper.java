package com.zjc.dao.mapper;

import com.zjc.dao.model.SysRoleMenu;

public interface SysRoleMenuMapper {
    int insert(SysRoleMenu record);

    int insertSelective(SysRoleMenu record);
}