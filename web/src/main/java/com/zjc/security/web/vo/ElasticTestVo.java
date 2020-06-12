package com.zjc.security.web.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName ElasticTestVo
 * @Description
 * @Author ZJC
 * @Date 2020/6/3 16:04
 */
@Data
@ToString
public class ElasticTestVo {

    private String id;
    private String name;
    private String sex;
    private String ethnic;
    private String age;
    private String profession;
    private String address;
    private String idcard;
    private String phone;
    private String contact;
    @JSONField(name = "contact_phone")
    private String contactPhone;



}
