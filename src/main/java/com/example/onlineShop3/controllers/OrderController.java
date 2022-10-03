package com.example.onlineShop3.controllers;

import com.example.onlineShop3.services.OrderService;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    public void addOrder(OrderVO orderVO){
        orderService.addOrder(orderVO);


    }
}
