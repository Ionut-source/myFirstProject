package com.example.onlineShop3.mappers;

import com.example.onlineShop3.controllers.entities.OrderItem;
import com.example.onlineShop3.controllers.entities.Orders;
import com.example.onlineShop3.controllers.entities.Product;
import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.exceptions.InvalidCustomerIdException;
import com.example.onlineShop3.exceptions.InvalidProductIdException;
import com.example.onlineShop3.exceptions.InvalidProductsException;
import com.example.onlineShop3.repositories.ProductRepository;
import com.example.onlineShop3.repositories.UserRepository;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public Orders toEntity(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException, InvalidProductIdException {
        if (orderVO == null) {
            return null;
        }
        validateOrder(orderVO);

        Orders orders = new Orders();

        Optional<User> userOptional = userRepository.findById(orderVO.getUserId().longValue());

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }

        orders.setUser(userOptional.get());

        Map<Integer, Integer> productsIdsToQuantityMap = orderVO.getProductsIdsToQuantity();
        List<OrderItem> orderItemsList = new ArrayList<>();

        Set<Integer> productIds = productsIdsToQuantityMap.keySet();

        for (Integer productId : productIds) {
            OrderItem orderItem = new OrderItem();

            Optional<Product> productOptional = productRepository.findById(productId.longValue());

            if (productOptional.isPresent()) {
                throw new InvalidProductIdException();
            }
            orderItem.setProduct(productOptional.get());

            Integer productQuantity = productsIdsToQuantityMap.get(productId);
            orderItem.setQuantity(productQuantity);
            orderItemsList.add(orderItem);
        }
        orders.setOrderItems(orderItemsList);
        return orders;
    }

    private void validateOrder(OrderVO orderVO) throws InvalidCustomerIdException, InvalidProductsException {
        if (orderVO.getProductsIdsToQuantity().keySet().isEmpty()) {
            throw new InvalidProductsException();
        }
    }
}
