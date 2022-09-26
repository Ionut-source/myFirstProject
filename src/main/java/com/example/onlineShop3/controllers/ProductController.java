package com.example.onlineShop3.controllers;

import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.exceptions.InvalidProductCodeException;
import com.example.onlineShop3.services.ProductService;
import com.example.onlineShop3.vos.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
private final ProductService productService;

    @PostMapping("/{customerId}")
    public void addProduct(@RequestBody ProductVO productVO, @PathVariable Long customerId){

        productService.addProduct(productVO, customerId);
    }

    @GetMapping("/{productCode}")
    public ProductVO getProduct(@PathVariable String productCode) throws InvalidProductCodeException {
       return productService.getProduct(productCode);
    }

    @GetMapping
    public List<ProductVO> getProducts(){
        return productService.getProducts();
    }
}
