package com.example.onlineShop3.repositories;

import com.example.onlineShop3.controllers.entities.Product;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static com.example.onlineShop3.enums.Currencies.USD;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findByCode_whenCodeIsPresentInDb_shouldReturnTheProduct() {
        Product product = new Product();
        product.setCode("111");
        product.setPrice(88);
        product.setStock(7);
        product.setValid(true);
        product.setCurrency(USD);
        product.setDescription("nesatisfacator");

        Product product2 = new Product();
        product.setCode("222");
        product.setPrice(88);
        product.setStock(7);
        product.setValid(true);
        product.setCurrency(USD);
        product.setDescription("nesatisfacator");

        testEntityManager.persist(product);
        testEntityManager.persist(product2);
        testEntityManager.flush();

        Optional<Product> productFromDb = productRepository.findByCode(product.getCode());

        assertThat(productFromDb).isPresent();
        assertThat(productFromDb.get().getCode()).isEqualTo(product.getCode());
    }

    @Test
    public void findByCode_whenCodeIsNotPresentInDb_shouldReturnEmpty() {

        Optional<Product> productFromDb = productRepository.findByCode("xxx");

        assertThat(productFromDb).isNotPresent();
    }


}