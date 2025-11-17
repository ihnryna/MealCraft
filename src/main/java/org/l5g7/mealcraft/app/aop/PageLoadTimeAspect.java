package org.l5g7.mealcraft.app.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PageLoadTimeAspect {

    private static final Logger perfLog = LoggerFactory.getLogger("performanceLogger");

    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void anyController() {
    }

    @Around("anyController()")
    public Object measurePageLoadTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            long end = System.nanoTime();
            long durationNs = end - start;
            double durationMs = durationNs / 1000000.0;

            String methodName = proceedingJoinPoint.getSignature().toShortString();
            perfLog.info(
                    "Page handled by {} in {} ms ({} ns)",
                    methodName, durationMs, durationNs
            );
        }
    }
}
