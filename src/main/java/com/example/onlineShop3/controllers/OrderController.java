package com.example.onlineShop3.controllers;

import com.example.onlineShop3.exceptions.InvalidCustomerIdException;
import com.example.onlineShop3.exceptions.InvalidProductIdException;
import com.example.onlineShop3.exceptions.InvalidProductsException;
import com.example.onlineShop3.services.OrderService;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public void addOrder(@RequestBody OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException {
        orderService.addOrder(orderVO);
    }
}
