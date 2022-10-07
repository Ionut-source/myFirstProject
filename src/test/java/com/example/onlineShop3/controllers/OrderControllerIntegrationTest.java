package com.example.onlineShop3.controllers;

import com.example.onlineShop3.vos.OrderVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static com.example.onlineShop3.enums.Roles.ADMIN;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

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


    @Test
    public void addOrder_whenOrderIsValid_shouldAddItInDb() {
       // utilComponent.saveUserWithRole();
        OrderVO orderVO = new OrderVO();
       // orderVO.setUserId();
    }

    @Test
    public void addOrder_whenRequestIsMadeByAdmin_shouldThrowAnException() {

    }

    @Test
    public void addOrder_whenRequestIsMadeByExpeditor_shouldThrowAnException() {

    }
}