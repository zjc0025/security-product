package com.zjc.dao.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SysUser implements Serializable {
    private Integer id;

    private String username;

    private String password;

    private String nickname;

    private String roles;

    private Boolean enabled;

    private Boolean accountnonexpired;

    private Boolean credentialsnonexpired;

    private Boolean accountnonlocked;

    private static final long serialVersionUID = 1L;

    List<SysRole> sysRoleList;

    List<SysMenu> sysMenuList;
}