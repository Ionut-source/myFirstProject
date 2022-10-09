package com.example.onlineShop3.controllers;

import com.example.onlineShop3.controllers.entities.OrderItem;
import com.example.onlineShop3.controllers.entities.Orders;
import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.repositories.OrderRepository;
import com.example.onlineShop3.utils.UtilComponent;
import com.example.onlineShop3.vos.OrderVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;

import static com.example.onlineShop3.enums.Roles.*;
import static com.example.onlineShop3.utils.UtilComponent.LOCALHOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpEntity.EMPTY;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrderControllerIntegrationTest {

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
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplateForPatch;

    @Autowired
    private UtilComponent utilComponent;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    @Transactional
    public void addOrder_whenOrderIsValid_shouldAddItInDb() {
        User user = utilComponent.saveUserWithRole(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("17", "19");

        OrderVO orderVO = createOrderVO(user, product);

        testRestTemplate.postForEntity(LOCALHOST + port +
                "/order", orderVO, Void.class);

        List<Orders> ordersIterable = (List<Orders>) orderRepository.findAll();
        Optional<OrderItem> orderItemOptional = ordersIterable.stream()
                .map(orders -> ((List<OrderItem>) orders.getOrderItems()))
                .flatMap(List::stream)
                .filter(orderItem -> orderItem.getProduct().getId() == product.getId())
                .findFirst();

        assertThat(orderItemOptional).isPresent();
    }

    @Test
    public void addOrder_whenRequestIsMadeByAdmin_shouldThrowAnException() {
        User user = utilComponent.saveUserWithRole(ADMIN);
        Product product = utilComponent.storeTwoProductsInDatabase("1234", "2345");

        OrderVO orderVO = createOrderVO(user, product);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(LOCALHOST + port +
                "/order", orderVO, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utiliztorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void addOrder_whenRequestIsMadeByExpeditor_shouldThrowAnException() {
        User user = utilComponent.saveUserWithRole(EXPEDITOR);
        Product product = utilComponent.storeTwoProductsInDatabase("246", "689");

        OrderVO orderVO = createOrderVO(user, product);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(LOCALHOST + port +
                "/order", orderVO, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utiliztorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldDeliverItByExpeditor() {
        User expeditor = utilComponent.saveUserWithRole(EXPEDITOR);
        User client = utilComponent.saveUserWithRole(CLIENT);

        Product product = utilComponent.storeTwoProductsInDatabase("35", "47");

        Orders orderWithProducts = generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        restTemplateForPatch.exchange(LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/" + expeditor.getId(),
                PATCH, EMPTY, Void.class);

        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();

        assertThat(orderFromDb.isDelivered()).isTrue();
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldNOTDeliverItByAdmin() {
        User adminAsExpeditor = utilComponent.saveUserWithRole(ADMIN);
        User client = utilComponent.saveUserWithRole(CLIENT);

        Product product = utilComponent.storeTwoProductsInDatabase("56", "68");

        Orders orderWithProducts = generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/" + adminAsExpeditor.getId(),
                    PATCH, EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utiliztorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldNOTDeliverItByClient() {
        User clientAsExpeditor = utilComponent.saveUserWithRole(CLIENT);
        User client = utilComponent.saveUserWithRole(CLIENT);

        Product product = utilComponent.storeTwoProductsInDatabase("88", "99");

        Orders orderWithProducts = generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(LOCALHOST + port + "/order/" + orderWithProducts.getId() + "/" + clientAsExpeditor.getId(),
                    PATCH, EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utiliztorul nu are permisiunea de a executa aceasta operatiune!]");
        }

    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsCanceled_shouldThrowAnException() {

    }

    private OrderVO createOrderVO(User user, Product product) {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId((int) user.getId());
        Map<Integer, Integer> orderMap = new HashMap<>();
        orderMap.put((int) product.getId(), 9);
        orderVO.setProductsIdsToQuantity(orderMap);
        return orderVO;
    }

    private Orders generateOrderItems(Product product, User user) {
        Orders order = new Orders();
        order.setUser(user);
        Collection<OrderItem> orderItems = new ArrayList<>();
        OrderItem orderItem = generateOrderItem(product);
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        return order;
    }

    private OrderItem generateOrderItem(Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setProduct(product);
        return orderItem;
    }

}