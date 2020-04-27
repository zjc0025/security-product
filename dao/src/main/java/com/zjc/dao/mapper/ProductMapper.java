package com.zjc.dao.mapper;

import com.zjc.dao.model.Product;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKeyWithBLOBs(Product record);

    int updateByPrimaryKey(Product record);

    @ResultMap("BaseResultMap")
    @Select("select * from product")
    List<Product> findAll();

    @ResultMap("BaseResultMap")
    @Select("select * from product where code = #{code}")
    Product findByCode(String code);
}