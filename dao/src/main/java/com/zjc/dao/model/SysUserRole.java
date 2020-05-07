package com.zjc.dao.model;

import java.io.Serializable;

public class SysUserRole implements Serializable {
    private Integer userId;

    private Integer roleId;

    private static final long serialVersionUID = 1L;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}