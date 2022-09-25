package com.example.onlineShop3.controllers;

import com.example.onlineShop3.services.ProductService;
import com.example.onlineShop3.vos.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
private final ProductService productService;

    @PostMapping("/{customerId}")
    public void addProduct(@RequestBody ProductVO productVO, @PathVariable Long customerId){
        productService.addProduct(productVO, customerId);
    }
}
