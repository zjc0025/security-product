package com.zjc.security.web.controller;

import com.zjc.dao.model.Product;
import com.zjc.security.web.service.ProductService;
import com.zjc.security.web.vo.ValidateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName HelloController
 * @Description
 * @Author ZJC
 * @Date 2020/4/21 10:09
 */
@Controller
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/product/index")
    public String index(){
        return "product/index";
    }

    @PostMapping("/product/list")
    @ResponseBody
    public List<Product> getProductList(){
        return productService.getAllProduct();
    }

    @GetMapping("/product/add")
    public String add(){
        return "product/add";
    }

    @GetMapping("/product/form")
    public String form(){
        return "product/form";
    }

    @PostMapping("/product/addProduct")
    @ResponseBody
    public void addProduct(Product product){
        productService.addProduct(product);
    }

    @PostMapping("/product/editProduct")
    @ResponseBody
    public void editProduct(Product product){
        productService.editProduct(product);
    }

    @GetMapping("/product/checkCode")
    @ResponseBody
    public ValidateVo checkProductCode(String code){
        ValidateVo validateVo = new ValidateVo();
        boolean res = productService.checkProductCode(code);
        validateVo.setValid(res);
        return validateVo;
    }

}
