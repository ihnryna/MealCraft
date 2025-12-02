package org.l5g7.mealcraft.app.aop.loginaspect;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LogInAspectUtils {

    private LogInAspectUtils(){

    }

    public static String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    public static Map<String, ArrayList<LocalDateTime>> filterOldLogs(Map<String, ArrayList<LocalDateTime>> oldLogs, int limitMinutes) {

        LocalDateTime now = LocalDateTime.now();

        return oldLogs.entrySet().stream()
                .map(entry -> { //filter timestamps
                    ArrayList<LocalDateTime> filtered = new ArrayList<>(
                            entry.getValue().stream()
                                    .filter(t -> t.plusMinutes(limitMinutes).isAfter(now))
                                    .toList()
                    );
                    return Map.entry(entry.getKey(), filtered);
                })
                .filter(e -> !e.getValue().isEmpty()) //remove empty entries
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        HashMap::new
                )); //collect back to hashmap
    }
}
