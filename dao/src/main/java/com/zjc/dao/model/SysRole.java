package com.zjc.dao.model;

import java.io.Serializable;
import java.util.Date;

public class SysRole implements Serializable {
    private Integer roleId;

    private String code;

    private String name;

    private String ord;

    private Date modifyTime;

    private static final long serialVersionUID = 1L;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getOrd() {
        return ord;
    }

    public void setOrd(String ord) {
        this.ord = ord == null ? null : ord.trim();
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}