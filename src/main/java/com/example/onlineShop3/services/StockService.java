package com.example.onlineShop3.services;

import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {
    private final ProductRepository productRepository;

    public boolean isHavingEnoughStock(Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId.longValue()).get();
        return product.getStock() >= quantity;
    }
}
