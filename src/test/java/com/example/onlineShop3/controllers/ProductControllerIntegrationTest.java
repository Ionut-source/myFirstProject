package com.example.onlineShop3.controllers;

import com.example.onlineShop3.controllers.entities.Address;
import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.enums.Roles;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.repositories.UserRepository;
import com.example.onlineShop3.vos.ProductVO;
import org.assertj.core.api.ObjectArrayAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static com.example.onlineShop3.enums.Currencies.*;
import static com.example.onlineShop3.enums.Roles.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerIntegrationTest {

    public static final String LOCALHOST = "http://localhost:";
    @LocalServerPort
    private int port;

    @Autowired
    private ProductController productController;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

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

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port + "/product/123", productVO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Id-ul trimis este invalid!");

    }

    @Test
    public void addProduct_whenUserIsNOTAdmin_shouldThrowInvalidOperationException() {
        User userEntity = saveUserWithRole(CLIENT);

        ProductVO productVO = new ProductVO();
        productVO.setCode("777");
        productVO.setPrice(100);
        productVO.setCurrency(USD);
        productVO.setStock(27);
        productVO.setDescription("minunat");
        productVO.setValid(true);

        ResponseEntity<String> response = testRestTemplate.postForEntity(LOCALHOST + port + "/product/" + userEntity.getId(), productVO, String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Utiliztorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void getProductByCode_whenCodeIsPresentInDb_shouldReturnTheProduct() {
        Product product = storeTwoProductsInDatabase("1010", "2020");

        ProductVO productResponse = testRestTemplate.getForObject(LOCALHOST + port + "/product/" + product.getCode(), ProductVO.class);

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
        storeTwoProductsInDatabase("1", "2");
        ProductVO[] products = testRestTemplate.getForObject(LOCALHOST + port + "/product", ProductVO[].class);

        ObjectArrayAssert<ProductVO> voObjectArrayAssert = assertThat(products).hasSize(2);
        assertThat(products[0].getCode()).contains("1");
        assertThat(products[1].getCode()).contains("2");
    }

    @Test
    public void updateProduct_whenUserIsEditor_shouldUpdateTheProduct(){
        Product product = generateProduct("110");
        productRepository.save(product);

        User user = saveUserWithRole(EDITOR);

        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setDescription("good");
        productVO.setCurrency(RON);
        productVO.setPrice(55.15);
        productVO.setValid(true);
        productVO.setStock(24);

        testRestTemplate.put(LOCALHOST + port + "/product/" +  user.getId(), productVO);

        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());

        assertThat(updatedProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(product.getCurrency());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updatedProduct.get().isValid()).isEqualTo(product.isValid());


    }

    @Test
    public void updateProduct_whenUserIsAdmin_shouldUpdateTheProduct(){
        Product product = generateProduct("111");
        productRepository.save(product);

        User user = saveUserWithRole(ADMIN);

        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setDescription("good");
        productVO.setCurrency(RON);
        productVO.setPrice(55.15);
        productVO.setValid(true);
        productVO.setStock(37);

        testRestTemplate.put(LOCALHOST + port + "/product/" +  user.getId(), productVO);

        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());

        assertThat(updatedProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(product.getCurrency());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updatedProduct.get().isValid()).isEqualTo(product.isValid());

    }

    @Test
    public void updateProduct_whenUserIsClient_shouldNOTUpdateTheProduct(){
        Product product = generateProduct("113");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product" + product.getCode() + "/1");

        assertThat(productRepository.findByCode(product.getCode())).isPresent();

        User user = saveUserWithRole(CLIENT);

        ProductVO productVO = new ProductVO();
        productVO.setCode(product.getCode());
        productVO.setDescription("good");
        productVO.setCurrency(RON);
        productVO.setPrice(55.15);
        productVO.setValid(true);
        productVO.setStock(25);

        testRestTemplate.put(LOCALHOST + port + "/product/" +  user.getId(), productVO);

        Optional<Product> updatedProduct = productRepository.findByCode(productVO.getCode());

        assertThat(updatedProduct.get().getDescription()).isEqualTo(product.getDescription());
        assertThat(updatedProduct.get().getCurrency()).isEqualTo(product.getCurrency());
        assertThat(updatedProduct.get().getPrice()).isEqualTo(product.getPrice());
        assertThat(updatedProduct.get().getStock()).isEqualTo(product.getStock());
        assertThat(updatedProduct.get().isValid()).isEqualTo(product.isValid());
    }

    @Test
    public void deleteProduct_whenUserIsAdmin_shouldDeleteTheProduct(){
        Product product = generateProduct("114");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product/" + product.getCode() + "/1");

        assertThat(productRepository.findByCode(product.getCode())).isNotPresent();
    }

    @Test
    public void deleteProduct_whenUserIsClient_shouldNOTDeleteTheProduct(){
        Product product = generateProduct("114");
        productRepository.save(product);

        testRestTemplate.delete(LOCALHOST + port + "/product/" + product.getCode() + "/2");

        assertThat(productRepository.findByCode(product.getCode())).isPresent();
    }



    private Product generateProduct(String productCode) {
        Product product = new Product();
        product.setCode(productCode);
        product.setDescription("good");
        product.setCurrency(RON);
        product.setPrice(55.15);
        product.setValid(true);
        product.setStock(24);
        return product;
    }

    private Product storeTwoProductsInDatabase(String code1, String code2) {
        Product product = generateProduct(code1);
        Product product2 = generateProduct(code2);

        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        products.add(product2);
        productRepository.saveAll(products);
        return product;
    }

    private User saveUserWithRole(Roles role) {
        User userEntity = new User();
        userEntity.setFirstname("IonutC");
        Collection<Roles> roles = new ArrayList<>();
        roles.add(role);
        userEntity.setRoles(roles);
        Address address = new Address();
        address.setCity("Bucuresti");
        address.setStreet("Crizantemelor");
        address.setNumber(2);
        address.setZipcode("11");
        userEntity.setAddress(address);
        userRepository.save(userEntity);
        return userEntity;
    }
}