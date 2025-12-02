package org.l5g7.mealcraft.app.aop.loginaspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.l5g7.mealcraft.app.aop.loginaspect.LogInAspectUtils.getClientIp;

@Aspect
@Component
public class LogInLimitAspect {

    private HashMap<String, ArrayList<LocalDateTime>> logCounts = new HashMap<>();

    private static final int LIMIT_MINUTES = 1;
    private static final int MAX_LOGS = 200;

    @Pointcut("execution(* org.l5g7.mealcraft.app.auth.AuthService.login(..))")
    public void loginMethod() {}

    @Around("loginMethod()")
    public  Object  limitLoginLogs(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attr != null;
        HttpServletRequest request = attr.getRequest();
        String clientIp = getClientIp(request);

        if (clientIp != null) {
            logCounts = (HashMap<String, ArrayList<LocalDateTime>>) LogInAspectUtils.filterOldLogs(logCounts, LIMIT_MINUTES);

            logCounts.putIfAbsent(clientIp, new ArrayList<>());
            ArrayList<LocalDateTime> logRec = logCounts.get(clientIp);
            if (logRec.size() >= MAX_LOGS) {
               writeLogLimitExceededResponse(attr);
                return null;
            }

            logRec.add(LocalDateTime.now());
            logCounts.put(clientIp, logRec);
        }

        return joinPoint.proceed();
   }

    private void writeLogLimitExceededResponse(ServletRequestAttributes attr) throws IOException {
        HttpServletResponse response = attr.getResponse();
        try {
            assert response != null;
        } catch (Exception e) {
            throw new IOException("Could not get HttpServletResponse in LogInLimitAspect");
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Too many login attempts from this IP address. Please try again later.\"}");
        response.getWriter().flush();
    }


}
