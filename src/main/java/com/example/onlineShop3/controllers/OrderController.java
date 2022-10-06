package com.example.onlineShop3.controllers;

import com.example.onlineShop3.exceptions.*;
import com.example.onlineShop3.services.OrderService;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public void addOrder(@RequestBody OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException, NotEnoughStockException {
        orderService.addOrder(orderVO);
    }

    @PatchMapping("/{orderId}/{customerId}")
    public void deliver(@PathVariable Integer orderId, @PathVariable Long customerId) throws InvalidOrderIdException {
        orderService.deliver(orderId, customerId);
    }
}
