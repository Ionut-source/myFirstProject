package com.example.onlineShop3.vos;

import lombok.Data;

import java.util.Map;

@Data
public class OrderVO {
    private Integer userId;
    private Map<Integer, Integer> productsIdsToQuantity;
}
