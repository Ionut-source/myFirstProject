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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
                .map(Orders::getOrderItems)
                .flatMap(List::stream)
                .filter(orderItem -> orderItem.getProduct().getId() == product.getId())
                .findFirst();

        //assertThat(orderItemOptional).isPresent();
    }

    @Test
    public void addOrder_whenRequestIsMadeByAdmin_shouldThrowAnException() {
        User user = utilComponent.saveUserWithRole(ADMIN);
        Product product = utilComponent.storeTwoProductsInDatabase("1234", "2345");

        OrderVO orderVO = createOrderVO(user, product);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(LOCALHOST + port +
                "/order", orderVO, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void addOrder_whenRequestIsMadeByExpeditor_shouldThrowAnException() {
        User user = utilComponent.saveUserWithRole(EXPEDITOR);
        Product product = utilComponent.storeTwoProductsInDatabase("246", "689");

        OrderVO orderVO = createOrderVO(user, product);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(LOCALHOST + port +
                "/order", orderVO, String.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(responseEntity.getBody()).isEqualTo("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldDeliverItByExpeditor() {
        User expeditor = utilComponent.saveUserWithRole(EXPEDITOR);
        User client = utilComponent.saveUserWithRole(CLIENT);

        Product product = utilComponent.storeTwoProductsInDatabase("35", "47");

        Orders orderWithProducts = utilComponent.generateOrderItems(product, client);
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

        Orders orderWithProducts = utilComponent.generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(LOCALHOST + port + "/order/" +
                            orderWithProducts.getId() + "/" + adminAsExpeditor.getId(),
                    PATCH, EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsNotCanceled_shouldNOTDeliverItByClient() {
        User clientAsExpeditor = utilComponent.saveUserWithRole(CLIENT);
        User client = utilComponent.saveUserWithRole(CLIENT);

        Product product = utilComponent.storeTwoProductsInDatabase("88", "99");

        Orders orderWithProducts = utilComponent.generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(LOCALHOST + port + "/order/" +
                            orderWithProducts.getId() + "/" + clientAsExpeditor.getId(),
                    PATCH, EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }

    }

    @Test
    public void deliver_whenHavingAnOrderWitchIsCanceled_shouldThrowAnException() {
        User expeditor = utilComponent.saveUserWithRole(EXPEDITOR);
        User client = utilComponent.saveUserWithRole(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("98 ", "87");

        Orders orderWithProducts = utilComponent.generateOrderItems(product, expeditor);
        orderWithProducts.setCanceled(true);
        orderRepository.save(orderWithProducts);

        try {
            ResponseEntity<String> responseEntity = restTemplateForPatch.exchange(LOCALHOST + port + "/order/" +
                            orderWithProducts.getId() + "/" + expeditor.getId(),
                    PATCH, EMPTY, String.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda a fost anulata!]");
        }
    }

    @Test
    public void cancel_whenValidOrder_shouldCancelIt() {
        User client = utilComponent.saveUserWithRole(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("77 ", "66");
        Orders orderWithProducts = utilComponent.generateOrderItems(product, client);
        orderRepository.save(orderWithProducts);

        restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/" + client.getId(),
                PATCH, EMPTY, Void.class);

        Orders orderFromDB = orderRepository.findById(orderWithProducts.getId()).get();

        assertThat(orderFromDB.isCanceled()).isTrue();
    }

    @Test
    public void cancel_whenOrderIsAlreadySent_shouldThrowAnException() {
        User client = utilComponent.saveUserWithRole(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("76 ", "65");

        Orders orderWithProducts = utilComponent.generateOrderItems(product, client);
        orderWithProducts.setDelivered(true);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/" + client.getId(),
                    PATCH, EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda a fost deja expediata!]");
        }
    }

    @Test
    public void cancel_whenUserIsAdmin_shouldThrowAnException() {
        User admin = utilComponent.saveUserWithRole(ADMIN);
        Product product = utilComponent.storeTwoProductsInDatabase("54 ", "43");
        Orders orderWithProducts = utilComponent.generateOrderItems(product, admin);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/" + admin.getId(),
                    PATCH, EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void cancel_whenUserIsAnExpeditor_shouldThrowAnException() {
        User expeditor = utilComponent.saveUserWithRole(EXPEDITOR);
        Product product = utilComponent.storeTwoProductsInDatabase("42 ", "31");
        Orders orderWithProducts = utilComponent.generateOrderItems(product, expeditor);
        orderRepository.save(orderWithProducts);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/cancel/" + orderWithProducts.getId() + "/" + expeditor.getId(),
                    PATCH, EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    @Transactional
    public void return_whenOrderValid_shouldReturnIt() {
        User client = utilComponent.saveUserWithRole(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("3r32 ", "3t13");
        Orders orderWithProducts = utilComponent.saveDeliveredOrder(client, product);

        restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() +
                "/" + client.getId(), PATCH, EMPTY, Void.class);
        Orders orderFromDb = orderRepository.findById(orderWithProducts.getId()).get();

        assertThat(orderFromDb.isReturned()).isTrue();
        assertThat(orderFromDb.getOrderItems().get(0).getProduct().getStock()).isEqualTo(product.getStock()
                + orderWithProducts.getOrderItems().get(0).getQuantity());
    }

    @Test
    public void return_whenOrderIsNotDelivered_shouldThrowException() {
        User client = utilComponent.saveUserWithRole(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("312 ", "358");
        Orders orderWithProducts = utilComponent.saveOrder(client, product);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/" + client.getId(),
                    PATCH, EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda nu poate fi returnata deoarece nu a fost livrata!]");
        }
    }

    @Test
    public void return_whenOrderIsCanceled_shouldThrowException() {
        User client = utilComponent.saveUserWithRole(CLIENT);
        Product product = utilComponent.storeTwoProductsInDatabase("27522 ", "78998");
        Orders orderWithProducts = utilComponent.saveCanceledAndDeliveredOrder(client, product);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() + "/" + client.getId(),
                    PATCH, EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Comanda a fost anulata!]");
        }
    }

    @Test
    public void return_whenUserIsAdmin_shouldThrowException() {
        User adminAsClient = utilComponent.saveUserWithRole(ADMIN);
        Product product = utilComponent.storeTwoProductsInDatabase("7524534 ", "314543");
        Orders orderWithProducts = utilComponent.saveOrder(adminAsClient, product);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() +
                    "/" + adminAsClient.getId(), PATCH, EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    @Test
    public void return_whenUserIsExpeditor_shouldThrowException() {
        User expeditorAsClient = utilComponent.saveUserWithRole(EXPEDITOR);
        Product product = utilComponent.storeTwoProductsInDatabase("5654y45 ", "89907e");
        Orders orderWithProducts = utilComponent.saveOrder(expeditorAsClient, product);

        try {
            restTemplateForPatch.exchange(LOCALHOST + port + "/order/return/" + orderWithProducts.getId() +
                    "/" + expeditorAsClient.getId(), PATCH, EMPTY, Void.class);
        } catch (RestClientException exception) {
            assertThat(exception.getMessage()).isEqualTo("400 : [Utilizatorul nu are permisiunea de a executa aceasta operatiune!]");
        }
    }

    private OrderVO createOrderVO(User user, Product product) {
        OrderVO orderVO = new OrderVO();
        orderVO.setUserId((int) user.getId());
        Map<Integer, Integer> orderMap = new HashMap<>();
        orderMap.put((int) product.getId(), 9);
        orderVO.setProductsIdsToQuantity(orderMap);
        return orderVO;
    }
}