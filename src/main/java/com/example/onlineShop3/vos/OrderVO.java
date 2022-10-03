package com.example.onlineShop3.vos;

import lombok.Data;

import java.util.List;

@Data
public class OrderVO {
    private Integer userId;
    private List<Integer> productsIds;
}
