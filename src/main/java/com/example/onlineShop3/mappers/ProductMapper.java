package com.example.onlineShop3.mappers;

import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.vos.ProductVO;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductVO productVO){
        if (productVO == null){
            return null;
        }

        Product product = new Product();
        product.setId(productVO.getId());
        product.setPrice(productVO.getPrice());
        product.setCode(productVO.getCode());
        product.setDescription(productVO.getDescription());
        product.setStock(productVO.getStock());
        product.setValid(productVO.isValid());
        product.setCurrency(productVO.getCurrency());
        return product;    }
}
