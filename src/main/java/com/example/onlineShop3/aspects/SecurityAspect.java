package com.example.onlineShop3.aspects;

import com.example.onlineShop3.controllers.entities.User;
import com.example.onlineShop3.enums.Roles;
import com.example.onlineShop3.exceptions.InvalidCustomerIdException;
import com.example.onlineShop3.exceptions.InvalidOperationException;
import com.example.onlineShop3.repositories.UserRepository;
import com.example.onlineShop3.vos.OrderVO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

import static com.example.onlineShop3.enums.Roles.*;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {
    private final UserRepository userRepository;

    @Pointcut("execution(* com.example.onlineShop3.services.ProductService.addProduct(..))")
    public void addProduct() {
    }

    @Pointcut("execution(* com.example.onlineShop3.services.ProductService.updateProduct(..))")
    public void updateProduct() {
    }

    @Pointcut("execution(* com.example.onlineShop3.services.ProductService.deleteProduct(..))")
    public void deleteProduct() {
    }

    @Pointcut("execution(* com.example.onlineShop3.services.ProductService.addStock(..))")
    public void addStock() {
    }

    @Pointcut("execution(* com.example.onlineShop3.services.OrderService.addOrder(..))")
    public void addOrderPointCut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.services.OrderService.deliver(..))")
    public void deliverPointCut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.services.OrderService.cancelOrder(..))")
    public void cancelOrderPointCut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.services.OrderService.returnOrder(..))")
    public void returnOrderPointCut() {
    }


    @Before("com.example.onlineShop3.aspects.SecurityAspect.addProduct()")
    public void checkSecurityBeforeAddingProduct(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToAddProduct(user.getRoles())) {
            throw new InvalidOperationException();

        }
        System.out.println(customerId);
    }

    @Before("com.example.onlineShop3.aspects.SecurityAspect.updateProduct()")
    public void checkSecurityBeforeUpdatingProduct(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToUpdateProduct(user.getRoles())) {
            throw new InvalidOperationException();

        }
        System.out.println(customerId);
    }

    @Before("com.example.onlineShop3.aspects.SecurityAspect.addStock()")
    public void checkSecurityBeforeAddingStock(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[2];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToAddStock(user.getRoles())) {
            throw new InvalidOperationException();

        }
        System.out.println(customerId);
    }


    @Before("com.example.onlineShop3.aspects.SecurityAspect.deleteProduct()")
    public void checkSecurityBeforeDeletingAProduct(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToDeleteProduct(user.getRoles())) {
            throw new InvalidOperationException();

        }
    }

    @Before("com.example.onlineShop3.aspects.SecurityAspect.addOrderPointCut()")
    public void checkSecurityBeforeAddingAnOrder(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        OrderVO orderVO = (OrderVO) joinPoint.getArgs()[0];

        if (orderVO.getUserId() == null) {
            throw new InvalidCustomerIdException();
        }
        Optional<User> userOptional = userRepository.findById(orderVO.getUserId().longValue());

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToAddAnOrder(user.getRoles())) {
            throw new InvalidOperationException();
        }
    }


    @Before("com.example.onlineShop3.aspects.SecurityAspect.deliverPointCut()")
    public void checkSecurityBeforeDeliver(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToDeliver(user.getRoles())) {
            throw new InvalidOperationException();

        }
        System.out.println(customerId);
    }

    @Before("com.example.onlineShop3.aspects.SecurityAspect.cancelOrderPointCut()")
    public void checkSecurityBeforeCancelingOrder(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToCancel(user.getRoles())) {
            throw new InvalidOperationException();

        }
        System.out.println(customerId);
    }

    @Before("com.example.onlineShop3.aspects.SecurityAspect.returnOrderPointCut()")
    public void checkSecurityBeforeReturningOrder(JoinPoint joinPoint)
            throws InvalidCustomerIdException, InvalidOperationException {
        Long customerId = (Long) joinPoint.getArgs()[1];
        Optional<User> userOptional = userRepository.findById(customerId);

        if (!userOptional.isPresent()) {
            throw new InvalidCustomerIdException();
        }
        User user = userOptional.get();

        if (userIsNotAllowedToReturnOrder(user.getRoles())) {
            throw new InvalidOperationException();

        }
        System.out.println(customerId);
    }

    private boolean userIsNotAllowedToReturnOrder(Collection<Roles> roles) {
        return !roles.contains(CLIENT);
    }

    private boolean userIsNotAllowedToCancel(Collection<Roles> roles) {
        return !roles.contains(CLIENT);
    }

    private boolean userIsNotAllowedToDeliver(Collection<Roles> roles) {
        return !roles.contains(EXPEDITOR);
    }

    private boolean userIsNotAllowedToAddAnOrder(Collection<Roles> roles) {
        return !roles.contains(CLIENT);
    }

    private boolean userIsNotAllowedToAddProduct(Collection<Roles> roles) {
        return !roles.contains(ADMIN);
    }

    private boolean userIsNotAllowedToDeleteProduct(Collection<Roles> roles) {
        return !roles.contains(ADMIN);
    }

    private boolean userIsNotAllowedToUpdateProduct(Collection<Roles> roles) {
        return !roles.contains(ADMIN) && !roles.contains(EDITOR);
    }

    private boolean userIsNotAllowedToAddStock(Collection<Roles> roles) {
        return !roles.contains(ADMIN);
    }
}
