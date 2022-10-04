package com.example.onlineShop3.handlers;

import com.example.onlineShop3.exceptions.InvalidCustomerIdException;
import com.example.onlineShop3.exceptions.InvalidProductCodeException;
import com.example.onlineShop3.exceptions.InvalidProductIdException;
import com.example.onlineShop3.exceptions.InvalidProductsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class OrderHandler {

    @ExceptionHandler(InvalidProductsException.class)
    public ResponseEntity<String> handleInvalidProductsException() {
        return status(BAD_REQUEST).body("Comanda dumneavoastra nu contine niciun produs!");
    }

    @ExceptionHandler(InvalidCustomerIdException.class)
    public ResponseEntity<String> handleInvalidCustomerIdException() {
        return status(BAD_REQUEST).body("Comanda dumneavoastra nu este asignata unui user valid!");
    }

    @ExceptionHandler(InvalidProductIdException.class)
    public ResponseEntity<String> handleInvalidProductException() {
        return status(BAD_REQUEST).body("Id-ul unui produs nu este valid in comanda curenta!");
    }
}
