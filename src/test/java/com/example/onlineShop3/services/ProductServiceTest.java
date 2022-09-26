package com.example.onlineShop3.services;

import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.mappers.ProductMapper;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.vos.ProductVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static com.example.onlineShop3.enums.Currencies.EUR;
import static com.example.onlineShop3.enums.Currencies.RON;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @TestConfiguration
    public static class ProductServiceTestContextConfiguration {

        @MockBean
        private ProductMapper productMapper;

        @MockBean
        private ProductRepository productRepository;

        @Bean
        public ProductService productService() {
            return new ProductService(productMapper, productRepository);
        }
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepository productRepository;


    @Test
    public void addProduct() {

        Product product = new Product();
        product.setCurrency(EUR);
        product.setPrice(454);
        product.setValid(true);
        product.setStock(1);
        product.setCode("646");

        when(productMapper.toEntity(any())).thenReturn(product);

        ProductVO productVO = new ProductVO();
        productVO.setValid(true);
        productVO.setDescription("formidabil");
        productVO.setStock(1);
        productVO.setPrice(11);
        productVO.setCurrency(RON);
        productVO.setId(1);
        productVO.setCode("646");

        Long customerId = 99L;
        productService.addProduct(productVO, customerId);

        verify(productMapper).toEntity(productVO);
        verify(productRepository).save(product);
    }
}