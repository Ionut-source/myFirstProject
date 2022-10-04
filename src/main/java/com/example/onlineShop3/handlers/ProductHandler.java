package com.example.onlineShop3.handlers;

import com.example.onlineShop3.exceptions.InvalidProductCodeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class ProductHandler {

    @ExceptionHandler(InvalidProductCodeException.class)
    public ResponseEntity<String> handleInvalidProductCodeException() {
        return status(BAD_REQUEST).body("Codul produsului trimis este invalid!");
    }
}
