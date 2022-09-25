package com.example.onlineShop3.repositories;

import com.example.onlineShop3.controllers.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository <User, Long> {
}
