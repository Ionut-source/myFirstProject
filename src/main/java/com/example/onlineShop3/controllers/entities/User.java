package com.example.onlineShop3.controllers.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    private String username;
    private String password;
    private String surname;
    private String firstname;
    @Embedded
    private Address address;

}
