package com.example.onlineShop3.services;

import com.example.onlineShop3.controllers.entities.OrderItem;
import com.example.onlineShop3.controllers.entities.Orders;
import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.exceptions.*;
import com.example.onlineShop3.mappers.OrderMapper;
import com.example.onlineShop3.repositories.OrderRepository;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final StockService stockService;

    public void addOrder(OrderVO orderVO)
            throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException, NotEnoughStockException {
        validateStock(orderVO);

        Orders order = orderMapper.toEntity(orderVO);
        for (OrderItem orderItem : order.getOrderItems()) {
            int oldProductStock = orderItem.getProduct().getStock();
            int productId = (int) orderItem.getProduct().getId();
            int sellStock = orderVO.getProductsIdsToQuantity().get(productId);
            orderItem.getProduct().setStock(oldProductStock - sellStock);
        }
        orderRepository.save(order);
    }

    @Transactional
    public void deliver(Integer orderId, Long customerId) throws InvalidOrderIdException, OrderCanceledException {
        System.out.println("Customer-ul cu id-ul: " + customerId + " este in service");
        throwExceptionIfOrderIdIsAbsent(orderId);

        Orders order = getOrderOrThrowException(orderId);
        if (order.isCanceled()) {
            throw new OrderCanceledException();
        }
        order.setDelivered(true);
    }

    @Transactional
    public void cancelOrder(Integer orderId, Long customerId)
            throws InvalidOrderIdException, OrderAlreadyDeliveredException {
        System.out.println("Customer-ul cu id-ul: " + customerId + " este in service pentru a anula comanda " + orderId);

        throwExceptionIfOrderIdIsAbsent(orderId);
        Orders order = getOrderOrThrowException(orderId);
        if (order.isDelivered()) {
            throw new OrderAlreadyDeliveredException();
        }
        order.setCanceled(true);
    }

    @Transactional
    public void returnOrder(Integer orderId, Long customerId)
            throws InvalidOrderIdException, OrderNotDeliveredYetException, OrderCanceledException {
        System.out.println("Customer-ul cu id-ul: " + customerId + " este in service pentru a returna comanda " + orderId);
        throwExceptionIfOrderIdIsAbsent(orderId);
        Orders order = getOrderOrThrowException(orderId);

        if (!order.isDelivered()) {
            throw new OrderNotDeliveredYetException();
        }

        if (order.isCanceled()) {
            throw new OrderCanceledException();
        }

        order.setReturned(true);
        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            int oldStock = product.getStock();
            product.setStock(oldStock + orderItem.getQuantity());

        });
    }

    private void throwExceptionIfOrderIdIsAbsent(Integer orderId) throws InvalidOrderIdException {
        if (orderId == null) {
            throw new InvalidOrderIdException();
        }
    }

    private Orders getOrderOrThrowException(Integer orderId) throws InvalidOrderIdException {
        Optional<Orders> orderOptional = orderRepository.findById(orderId.longValue());
        if (!orderOptional.isPresent()) {
            throw new InvalidOrderIdException();
        }
        return orderOptional.get();
    }

    private void validateStock(OrderVO orderVO) throws NotEnoughStockException {
        Map<Integer, Integer> productsIdsToQuantityMap = orderVO.getProductsIdsToQuantity();
        Set<Integer> productIds = productsIdsToQuantityMap.keySet();
        for (Integer productId : productIds) {
            Integer quantity = productsIdsToQuantityMap.get(productId);
            boolean havingEnoughStock = stockService.isHavingEnoughStock(productId, quantity);
            if (!havingEnoughStock) {
                throw new NotEnoughStockException();
            }
        }
    }

}
