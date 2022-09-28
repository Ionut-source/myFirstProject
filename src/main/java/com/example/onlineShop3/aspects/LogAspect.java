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
    public void addProductPointCut() {
    }

    @Before("com.example.onlineShop3.aspects.LogAspect.addProductPointCut()")
    public void before(JoinPoint joinPoint) {
        System.out.println("In before aspect at " + new Date());
        System.out.println("ProductVO:" + joinPoint.getArgs()[0]);
        System.out.println("The user had id " + joinPoint.getArgs()[1]);

    }

    @After("com.example.onlineShop3.aspects.LogAspect.addProductPointCut()")
    public void after(JoinPoint joinPoint) {
        System.out.println("In after aspect at " + new Date());
    }
}
