package com.example.onlineShop3.controllers.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
public class Address {
    private String city;
    private String street;
    private long number;
    private String zipcode;

}
