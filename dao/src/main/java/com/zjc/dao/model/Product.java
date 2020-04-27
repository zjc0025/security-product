package com.zjc.dao.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {
    private Integer id;

    private String code;

    private String productName;

    private Integer sample;

    private Integer storeInventory;

    private Integer warehouse;

    private Integer ordered;

    private Integer otherPlaceSample;

    private BigDecimal amount;

    private String remark;

    private byte[] picture;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Integer getSample() {
        return sample;
    }

    public void setSample(Integer sample) {
        this.sample = sample;
    }

    public Integer getStoreInventory() {
        return storeInventory;
    }

    public void setStoreInventory(Integer storeInventory) {
        this.storeInventory = storeInventory;
    }

    public Integer getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Integer warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getOrdered() {
        return ordered;
    }

    public void setOrdered(Integer ordered) {
        this.ordered = ordered;
    }

    public Integer getOtherPlaceSample() {
        return otherPlaceSample;
    }

    public void setOtherPlaceSample(Integer otherPlaceSample) {
        this.otherPlaceSample = otherPlaceSample;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    private String pictureBase64;

    public String getPictureBase64() {
        if(this.picture == null) return "";
        return new String(this.picture);
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }
}