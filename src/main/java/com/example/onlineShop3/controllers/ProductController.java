package com.example.onlineShop3.controllers;

import com.example.onlineShop3.exceptions.InvalidProductCodeException;
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
    public void addProduct(@RequestBody ProductVO productVO, @PathVariable Long customerId) {

        productService.addProduct(productVO, customerId);
    }

    @GetMapping("/{productCode}")
    public ProductVO getProduct(@PathVariable String productCode) throws InvalidProductCodeException {
        return productService.getProduct(productCode);
    }

    @GetMapping
    public ProductVO[] getProducts() {
        return productService.getProducts().toArray(new ProductVO[]{});
    }

    @PutMapping("/{customerId}")
    public void updateProduct(@RequestBody ProductVO productVO, Long customerId) throws InvalidProductCodeException {
        productService.updateProduct(productVO, customerId);
    }

    @DeleteMapping("/{productCode}/{customerId}")
    public void deleteProduct(@PathVariable String productCode, @PathVariable Long customerId) throws InvalidProductCodeException {
        productService.deleteProduct(productCode, customerId);
    }
}
