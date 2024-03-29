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
    public void addOrder(@RequestBody OrderVO orderVO)
            throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException, NotEnoughStockException {
        orderService.addOrder(orderVO);
    }

    @PatchMapping("/{orderId}/{customerId}")
    public void deliver(@PathVariable Integer orderId, @PathVariable Long customerId)
            throws InvalidOrderIdException, OrderCanceledException {
        orderService.deliver(orderId, customerId);
    }

    @PatchMapping("/cancel/{orderId}/{customerId}")
    public void cancelOrder(@PathVariable Integer orderId, @PathVariable Long customerId)
            throws OrderAlreadyDeliveredException, InvalidOrderIdException {
        orderService.cancelOrder(orderId, customerId);
    }

    @PatchMapping("/return/{orderId}/{customerId}")
    public void returnOrder(@PathVariable Integer orderId, @PathVariable Long customerId)
            throws InvalidOrderIdException, OrderNotDeliveredYetException, OrderCanceledException {
        orderService.returnOrder(orderId, customerId);
    }
}
