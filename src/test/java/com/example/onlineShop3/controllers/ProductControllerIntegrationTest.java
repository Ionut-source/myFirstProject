package com.example.onlineShop3.controllers;

import com.example.onlineShop3.controllers.entities.Address;
import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.enums.Roles;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.repositories.UserRepository;
import com.example.onlineShop3.utils.UtilComponent;
import com.example.onlineShop3.vos.ProductVO;
import org.assertj.core.api.ObjectArrayAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.example.onlineShop3.enums.Currencies.RON;
import static com.example.onlineShop3.enums.Currencies.USD;
import static com.example.onlineShop3.enums.Roles.*;
import static com.example.onlineShop3.utils.UtilComponent.LOCALHOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerIntegrationTest {

    @TestConfiguration
    static class ProductControllerIntegrationTestContextConfiguration {
        @Bean
        public RestTemplate restTemplateForPatch() {
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private ProductController productController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplateForPatch;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UtilComponent utilComponent;


    @Test
    public void contextLoads() {
        assertThat(productController).isNotNull();
    }

    @Test
    public void addProduct_whenUserIsAdmin_shouldStoreTheProduct() {
        User userEntity = new User();
        userEntity.setFirstname("IonutC");
        Collection<Roles> roles = new ArrayList<>();
        roles.add(ADMIN);
        userEntity.setRoles(roles);
        Address address = new Address();
        address.setCity("Bucuresti");
        address.setStreet("Crizantemelor");
        address.setNumber(2);
        address.setZipcode("11");
        userEntity.setAddress(address);
        userRepository.save(userEntity);

        ProductVO productVO = new ProductVO();
        productVO.setCode("777");
        productVO.setPrice(100);
        productVO.setCurrency(USD);
        productVO.setStock(27);
        productVO.setDescription("minunat");
        productVO.setValid(true);

        testRestTemplate.postForEntity(LOCALHOST + port + "/product/" + userEntity.getId(), productVO, Void.class);

        Iterable<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);

        Product product = products.iterator().next();

        assertThat(productVO.getCode()).isEqualTo(productVO.getCode());
    }

    @Test
    public void addProduct_whenUserIsNotInDb_shouldThrowInvalidCustomerIdException() {
        ProductVO productVO = new ProductVO();
        productVO.setCode("777");
        productVO.setPrice(100);
        productVO.setCurrency(USD);
        productVO.setStock(27);
        productVO.setDescription("minunat");
        productVO.setValid(true);

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port +
                "/product/123", productVO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Comanda dumneavoastra nu este asignata unui user valid!");
    }

    @Test
    public void addProduct_whenUserIsNOTAdmin_shouldThrowInvalidOperationException() {
        User userEntity = utilComponent.saveUserWithRole(CLIENT);

        ProductVO productVO = new ProductVO();
        productVO.setCode("777");
        productVO.setPrice(100);
        productVO.setCurrency(USD);
        productVO.setStock(27);
        productVO.setDescription("minunat");
        productVO.setValid(true);

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port + "/product/" +
                userEntity.getId(), productVO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void getProductByCode_whenCodeIsPresentInDb_shouldReturnTheProduct() {
        Product product = utilComponent.storeTwoProductsInDatabase("1010", "2020");

        ProductVO productResponse = testRestTemplate.getForObject(LOCALHOST + port + "/product/" +
                product.getCode(), ProductVO.class);

        assertThat(productResponse.getCode()).isEqualTo(product.getCode());
    }

    @Test
    public void getProductByCode_whenProductCodeIsNotPresent_shouldReturnErrorMessage() {
        String response = testRestTemplate.getForObject(LOCALHOST + port + "/product/12321", String.class);

        assertThat(response).isEqualTo("Codul produsului trimis este invalid!");
    }

    @Test
    public void getProducts() {
        productRepository.deleteAll();
        utilComponent.storeTwoProductsInDatabase("1", "2");
        ProductVO[] products = testRestTemplate.getForObject(LOCALHOST + port + "/product", ProductVO[].class);

        ObjectArrayAssert<ProductVO> voObjectArrayAssert = assertThat(products).hasSize(2);
        assertThat(products[0].getCode()).contains("1");
        assertThat(products[1].getCode()).contains("2");
    }

    @Test
    public void updateProduct_whenUserIsEditor_shouldUpdateTheProduct() {
        Product product = utilComponent.generateProduct("110");
        productRepository.save(product);

        User user = utilComponent.saveUserWithRole(EDITOR);

        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setDescription("good");
        productVO.setCurrency(RON);
        productVO.setPrice(55.15);
        productVO.setValid(true);
        productVO.setStock(24);

        testRestTemplate.put(LOCALHOST + port + "/product/" + user.getId(), productVO);

        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());

        assertThat(updatedProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(product.getCurrency());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updatedProduct.get().isValid()).isEqualTo(product.isValid());
    }

    @Test
    public void updateProduct_whenUserIsAdmin_shouldUpdateTheProduct() {
        Product product = utilComponent.generateProduct("111");
        productRepository.save(product);

        User user = utilComponent.saveUserWithRole(ADMIN);

        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setDescription("good");
        productVO.setCurrency(RON);
        productVO.setPrice(55.15);
        productVO.setValid(true);
        productVO.setStock(37);

        testRestTemplate.put(LOCALHOST + port + "/product/" + user.getId(), productVO);

        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());

        assertThat(updatedProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(product.getCurrency());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updatedProduct.get().isValid()).isEqualTo(product.isValid());
    }

    @Test
    public void updateProduct_whenUserIsClient_shouldNOTUpdateTheProduct() {
        Product product = utilComponent.generateProduct("113");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product" + product.getCode() + "/1");

        assertThat(productRepository.findByCode(product.getCode())).isPresent();

        User user = utilComponent.saveUserWithRole(CLIENT);

        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setDescription("good");
        productVO.setCurrency(RON);
        productVO.setPrice(55.15);
        productVO.setValid(true);
        productVO.setStock(25);

        testRestTemplate.put(LOCALHOST + port + "/product/" + user.getId(), productVO);

        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());

        assertThat(updatedProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(product.getCurrency());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updatedProduct.get().isValid()).isEqualTo(product.isValid());
    }

    @Test
    public void deleteProduct_whenUserIsAdmin_shouldDeleteTheProduct() {
        Product product = utilComponent.generateProduct("114");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product/" + product.getCode() + "/1");

        assertThat(productRepository.findByCode(product.getCode())).isNotPresent();
    }

    @Test
    public void deleteProduct_whenUserIsClient_shouldNOTDeleteTheProduct() {
        Product product = utilComponent.generateProduct("114");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product/" + product.getCode() + "/2");

        assertThat(productRepository.findByCode(product.getCode())).isPresent();
    }

    @Test
    public void addStock_whenAddingStockToAnItemByAdmin_shouldBeSaveIsnDB() {
        Product product = utilComponent.generateProduct("115");
        productRepository.save(product);

        User user = utilComponent.saveUserWithRole(ADMIN);

        restTemplateForPatch.exchange(LOCALHOST + port + "/product/" + product.getCode() + "/3/" + user.getId(),
                PATCH, EMPTY, Void.class);

        Product productFromDb = productRepository.findByCode(product.getCode()).get();
        assertThat(productFromDb.getStock()).isEqualTo(27);

    }
}