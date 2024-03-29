package com.example.onlineShop3.controllers.entities;

import com.example.onlineShop3.enums.Roles;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

import static javax.persistence.EnumType.STRING;
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
    private String firstname;
    private String surname;
    @Embedded
    private Address address;
    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "roles")
    @Enumerated(STRING)
    private Collection<Roles> roles;
}
