package com.example.FlightBooking.Aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.FlightBooking.Service.*.*(..))")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        logger.info("Executing {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning("execution(* com.example.FlightBooking.Service.*.*(..))")
    public void afterServiceMethod(JoinPoint joinPoint) {
        logger.info("Completed {}", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(value = "execution(* com.example.FlightBooking.Service.*.*(..))", throwing = "ex")
    public void afterThrowingServiceMethod(JoinPoint joinPoint, Exception ex) {
        logger.error("Exception in {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }
}
