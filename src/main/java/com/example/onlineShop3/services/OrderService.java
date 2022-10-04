package com.example.onlineShop3.services;

import com.example.onlineShop3.controllers.entities.Orders;
import com.example.onlineShop3.exceptions.InvalidCustomerIdException;
import com.example.onlineShop3.exceptions.InvalidProductIdException;
import com.example.onlineShop3.exceptions.InvalidProductsException;
import com.example.onlineShop3.mappers.OrderMapper;
import com.example.onlineShop3.repositories.OrderRepository;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public void addOrder(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException {
        Orders order = orderMapper.toEntity(orderVO);
        orderRepository.save(order);
    }
}
