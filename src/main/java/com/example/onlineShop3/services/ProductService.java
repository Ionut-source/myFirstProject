package com.example.onlineShop3.services;

import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.exceptions.InvalidProductCodeException;
import com.example.onlineShop3.mappers.ProductMapper;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.vos.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;

    public void addProduct(ProductVO productVO, Long customerId) {
        System.out.println("Customer with id " + customerId + "  is in service");
        Product product = productMapper.toEntity(productVO);
        productRepository.save(product);
    }

    public ProductVO getProduct(String productCode) throws InvalidProductCodeException {
        Product product = getProductEntity(productCode);

        return productMapper.toVO(product);
    }

    public List<ProductVO> getProducts() {
        List<ProductVO> products = new ArrayList<>();
        Iterable<Product> productsFromDbIterable = productRepository.findAll();
        for (Product product : productsFromDbIterable) {
            ProductVO productVO = productMapper.toVO(product);
            products.add(productVO);
        }
        return products;
    }

    public void updateProduct(ProductVO productVO, Long customerId) throws InvalidProductCodeException {
        System.out.println("Customer with id " + customerId + "is in service for update");
        if (productVO.getCode() == null) {
            throw new InvalidProductCodeException();
        }

        Product product = getProductEntity(productVO.getCode());
        product.setId(100L);
        product.setValid(productVO.isValid());
        product.setPrice(productVO.getPrice());
        product.setCurrency(productVO.getCurrency());
        product.setStock(productVO.getStock());
        product.setDescription(productVO.getDescription());

        productRepository.save(product);
    }

    public void deleteProduct(String productCode, Long customerId) throws InvalidProductCodeException {
        System.out.println("User with id: " + customerId + " is deleting " + productCode);
        verifyProductCode(productCode);

        Product product = getProductEntity(productCode);
        productRepository.delete(product);
    }

    @Transactional
    public void addStock(String productCode, Integer quantity, Long customerId) throws InvalidProductCodeException {
        System.out.println("User with id: " + customerId + " is adding stock for " + productCode +
                " number of items: " + quantity);
        verifyProductCode(productCode);
        Product product = getProductEntity(productCode);

        int oldStock = product.getStock();
        product.setStock(oldStock + quantity);
    }


    private Product getProductEntity(String productCode) throws InvalidProductCodeException {
        Optional<Product> productOptional = productRepository.findByCode(productCode);

        if (!productOptional.isPresent()) {
            throw new InvalidProductCodeException();
        }

        return productOptional.get();
    }

    private void verifyProductCode(String productCode) throws InvalidProductCodeException {
        if (productCode == null) {
            throw new InvalidProductCodeException();
        }
    }
}
