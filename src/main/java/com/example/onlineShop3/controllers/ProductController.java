package com.example.onlineShop3.controllers;

import com.example.onlineShop3.services.ProductService;
import com.example.onlineShop3.vos.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
private final ProductService productService;

    @PostMapping
    public void addProduct(@RequestBody ProductVO productVO){
        productService.addProduct(productVO);
    }
}
