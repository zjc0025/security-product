package com.zjc.security.web.service;

import com.zjc.dao.mapper.ProductMapper;
import com.zjc.dao.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ProductService
 * @Description
 * @Author ZJC
 * @Date 2020/4/23 9:14
 */
@Service
public class ProductService {

    @Autowired
    ProductMapper productMapper;

    public List<Product> getAllProduct(){
        return productMapper.findAll();
    }

    public void addProduct(Product product) {
        productMapper.insert(product);
    }

    public void editProduct(Product product) {
        productMapper.updateByPrimaryKey(product);
    }

    public boolean checkProductCode(String code) {
        Product product = productMapper.findByCode(code);
        return product == null;
    }
}
