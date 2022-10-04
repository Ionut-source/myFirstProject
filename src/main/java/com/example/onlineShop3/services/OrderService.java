package com.example.onlineShop3.services;

import com.example.onlineShop3.repositories.OrderRepository;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void addOrder(OrderVO orderVO){

    }
}
