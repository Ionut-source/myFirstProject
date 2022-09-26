package com.example.onlineShop3.controllers;

import com.example.onlineShop3.controllers.entities.Address;
import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.enums.Roles;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.repositories.UserRepository;
import com.example.onlineShop3.vos.ProductVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collection;

import static com.example.onlineShop3.enums.Currencies.USD;
import static com.example.onlineShop3.enums.Roles.ADMIN;
import static com.example.onlineShop3.enums.Roles.CLIENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ProductControllerTest {

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

        testRestTemplate.postForEntity("http://localhost:" + port + "/product/" + userEntity.getId(), productVO, Void.class);

        Iterable<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);

        Product product = products.iterator().next();

        assertThat(productVO.getCode()).isEqualTo(productVO.getCode());
    }

    @Test
    public void addProduct_whenUserIsNotInDb_shouldThrowInvalidCustomerIdException(){
       ProductVO productVO = new ProductVO();
       productVO.setCode("777");
       productVO.setPrice(100);
       productVO.setCurrency(USD);
       productVO.setStock(27);
       productVO.setDescription("minunat");
       productVO.setValid(true);

       ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/product/123", productVO, String.class);

       assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
       assertThat(response.getBody()).isEqualTo("Id-ul trimis este invalid!");

    }

   @Test
   public void addProduct_whenUserIsNOTAdmin_shouldThrowInvalidOperationException() {
      User userEntity = new User();
      userEntity.setFirstname("IonutC");
      Collection<Roles> roles = new ArrayList<>();
      roles.add(CLIENT);
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

      ResponseEntity<String> response = testRestTemplate.postForEntity("http://localhost:" + port + "/product/" + userEntity.getId(), productVO, String.class);

     assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
     assertThat(response.getBody()).isEqualTo("Utiliztorul nu are permisiunea de a executa aceasta operatiune!");
   }

}