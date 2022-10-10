package com.example.onlineShop3.controllers.entities;

import com.example.onlineShop3.enums.Currencies;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.AUTO;

@Entity
@Setter
@Getter
public class Product {

    @Id
    @GeneratedValue(strategy = AUTO)
    private long id;
    @Column(unique = true)
    private String code;
    private String description;
    private double price;
    private int stock;
    private boolean valid;
    @Enumerated(STRING)
    private Currencies currency;
}
