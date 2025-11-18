package org.l5g7.mealcraft.app.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ExceptionHandlingAspect {

    private static final Logger exceptionLog = LoggerFactory.getLogger("exceptionLogger");

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void anyServiceBean() {}

    @AfterThrowing(pointcut = "anyServiceBean()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        String args = Arrays.toString(joinPoint.getArgs());

        exceptionLog.error(
                "Service exception in method {} with args {}: {}",
                methodName, args, ex.getMessage(), ex
        );
    }
}
