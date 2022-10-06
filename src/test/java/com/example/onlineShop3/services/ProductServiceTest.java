package com.example.onlineShop3.services;

import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.exceptions.InvalidProductCodeException;
import com.example.onlineShop3.mappers.ProductMapper;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.vos.ProductVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static com.example.onlineShop3.enums.Currencies.EUR;
import static com.example.onlineShop3.enums.Currencies.RON;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    public void getProduct_whenProductIsNotInDb_shouldThrowAnException() {
        try {
            productService.getProduct("asd");
        } catch (InvalidProductCodeException e) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void getProduct_whenProductIsInDb_shouldTReturnIt() throws InvalidProductCodeException {
        Product product = new Product();
        product.setCode("777");
        when(productRepository.findByCode(any())).thenReturn(of(product));
        ProductVO productVO = new ProductVO();
        productVO.setCode("777");
        when(productMapper.toVO(any())).thenReturn(productVO);

        ProductVO returnedProduct = productService.getProduct("777");

        assertThat(returnedProduct.getCode()).isEqualTo("777");

        verify(productRepository).findByCode("777");
        verify(productMapper).toVO(product);
    }

    @Test
    public void getProducts_() {
        ArrayList<Product> products = new ArrayList<>();
        Product tv = new Product();
        tv.setCode("777");
        products.add(tv);
        Product tablet = new Product();
        tablet.setCode("777");
        products.add(tablet);

        ProductVO tvVO = new ProductVO();
        tvVO.setCode("7772");
        ProductVO tabletVO = new ProductVO();
        tabletVO.setCode("7772");

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toVO(tv)).thenReturn(tvVO);
        when(productMapper.toVO(tablet)).thenReturn(tabletVO);

        List<ProductVO> productList = productService.getProducts();

        assertThat(productList).hasSize(2);
        assertThat(productList).containsOnly(tvVO, tabletVO);

        verify(productRepository).findAll();
        verify(productMapper).toVO(tv);
        verify(productMapper).toVO(tablet);
    }

    @Test
    public void updateProduct_whenProductCodeIsNull_shouldThrownAnException() {
        ProductVO tvVO = new ProductVO();
        try {
            productService.updateProduct(tvVO, 1L);
        } catch (InvalidProductCodeException e) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void updateProduct_whenProductCodeIsInvalid_shouldThrownAnException() {
        ProductVO tvVO = new ProductVO();
        tvVO.setCode("asd");
        try {
            productService.updateProduct(tvVO, 1L);
        } catch (InvalidProductCodeException e) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void updateProduct_whenProductCodeIsValid_shouldUpdateTheProduct() throws InvalidProductCodeException {
        ProductVO tvVO = new ProductVO();
        tvVO.setCode("777");
        tvVO.setCurrency(EUR);

        Product tv = new Product();
        tv.setCode("777");
        tv.setCurrency(RON);

        when(productRepository.findByCode(any())).thenReturn(of(tv));

        productService.updateProduct(tvVO, 1L);

        verify(productRepository).findByCode(tv.getCode());

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());

        Product productSendAsCapture = productArgumentCaptor.getValue();

        assertThat(productSendAsCapture.getCurrency()).isEqualTo(tvVO.getCurrency());
    }

    @Test
    public void deleteProduct_whenCodeIsNull_shouldThrownAnException() {
        try {
            productService.deleteProduct(null, 1L);
        } catch (InvalidProductCodeException e) {
            assert true;
            return;
        }
        assert false;
    }

    @Test
    public void deleteProduct_whenCodeIsValid_shouldDeleteTheProduct() throws InvalidProductCodeException {
        Product telephone = new Product();
        telephone.setCode("24");
        when(productRepository.findByCode(any())).thenReturn(of(telephone));

        productService.deleteProduct("111", 1L);

        verify(productRepository).findByCode("111");
        verify(productRepository).delete(telephone);


    }
}
