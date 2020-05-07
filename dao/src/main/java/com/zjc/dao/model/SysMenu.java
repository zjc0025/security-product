package com.zjc.dao.model;

import java.io.Serializable;

public class SysMenu implements Serializable {
    private Integer menuId;

    private String code;

    private String name;

    private String url;

    private Integer orderNo;

    private String ico;

    private Integer menuIdParent;

    private static final long serialVersionUID = 1L;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico == null ? null : ico.trim();
    }

    public Integer getMenuIdParent() {
        return menuIdParent;
    }

    public void setMenuIdParent(Integer menuIdParent) {
        this.menuIdParent = menuIdParent;
    }
}