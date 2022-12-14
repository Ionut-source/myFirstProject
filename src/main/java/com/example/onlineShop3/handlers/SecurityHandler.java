package com.example.onlineShop3.handlers;

import com.example.onlineShop3.exceptions.InvalidCustomerIdException;
import com.example.onlineShop3.exceptions.InvalidOperationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class SecurityHandler {

    @ExceptionHandler(InvalidCustomerIdException.class)
    public ResponseEntity<String> handlerInvalidCustomerIdException() {
        return status(BAD_REQUEST).body("Id-ul trimis este invalid!");
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<String> handlerInvalidOperationException() {
        return status(BAD_REQUEST).body("Utilizatorul nu are permisiunea de a executa aceasta operatiune!");
    }
}
