package com.example.onlineShop3.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.example.onlineShop3.controllers.ProductController.addProduct(..))")
    public void addProductPointcut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.controllers.ProductController.updateProduct(..))")
    public void updateProductPointcut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.controllers.OrderController.addOrder(..))")
    public void addOrderPointcut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.controllers.OrderController.deliverOrder(..))")
    public void deliverOrderPointcut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.controllers.OrderController.cancelOrder(..))")
    public void cancelOrderPointcut() {
    }

    @Pointcut("execution(* com.example.onlineShop3.controllers.OrderController.returnOrder(..))")
    public void returnOrderPointcut() {
    }

    @Before("com.example.onlineShop3.aspects.LogAspect.addProductPointcut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date());
        System.out.println("ProductVO:" + joinPoint.getArgs()[0]);
        System.out.println("The user had id " + joinPoint.getArgs()[1]);
    }

    @Before("com.example.onlineShop3.aspects.LogAspect.updateProductPointcut()")
    public void beforeUpdate(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing an update");
        System.out.println("ProductVO:" + joinPoint.getArgs()[0]);
        System.out.println("The user had id " + joinPoint.getArgs()[1]);
    }

    @Before("com.example.onlineShop3.aspects.LogAspect.deliverOrderPointcut()")
    public void beforeDeliver(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing a deliver");
        System.out.println("Order Id :" + joinPoint.getArgs()[0]);
        System.out.println("The user had id " + joinPoint.getArgs()[1]);
    }

    @Before("com.example.onlineShop3.aspects.LogAspect.cancelOrderPointcut()")
    public void beforeCancel(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing a cancelation");
        System.out.println("Order Id :" + joinPoint.getArgs()[0]);
        System.out.println("The user had id " + joinPoint.getArgs()[1]);
    }

    @Before("com.example.onlineShop3.aspects.LogAspect.returnOrderPointcut()")
    public void beforeReturningOrder(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date() + " for doing a return");
        System.out.println("Order Id :" + joinPoint.getArgs()[0]);
        System.out.println("The user had id " + joinPoint.getArgs()[1]);
    }

    @After("com.example.onlineShop3.aspects.LogAspect.addProductPointcut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("In after aspect at " + new Date());
    }

    @Before("com.example.onlineShop3.aspects.LogAspect.addOrderPointcut()")
    public void beforeAddingAnOrder(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date());
        System.out.println("OrderVO:" + joinPoint.getArgs()[0]);
    }
}
