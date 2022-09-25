package com.example.onlineShop3.aspects;

import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.exceptions.InvalidCustomerIdException;
import com.example.onlineShop3.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {
    private final UserRepository userRepository;

    @Pointcut("execution(* com.example.onlineShop3.services.ProductService.addProduct(..))")
    public void addProduct() {
    }

    @Before("com.example.onlineShop3.aspects.SecurityAspect.addProduct()")
    public void checkSecurityBeforeAddingProduct(JoinPoint joinPoint) throws InvalidCustomerIdException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();
        System.out.println(customerId);
    }
}
