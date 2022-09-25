package com.example.onlineShop3.vos;

import com.example.onlineShop3.enums.Currencies;
import lombok.Data;

@Data
public class ProductVO {
    private long id;
    private String code;
    private String description;
    private double price;
    private int stock;
    private boolean valid;
    private Currencies currency;
}
