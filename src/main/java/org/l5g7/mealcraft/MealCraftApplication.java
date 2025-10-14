package org.l5g7.mealcraft;

import org.slf4j.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MealCraftApplication {

    private static final Logger log = LoggerFactory.getLogger(MealCraftApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MealCraftApplication.class, args);
        log.info("MEALCRAFT successfully started!");
        MDC.put("user", "iryna");
        try {
            log.info("Start processing request");
            log.info("Request processed successfully");
            Marker CRITICAL = MarkerFactory.getMarker("CRITICAL");
            log.warn(CRITICAL, "Something critical happened!");

        } finally {
            MDC.remove("user");
        }
        log.warn("Something looks off...");
        log.error("Oops! Critical error!");
    }

}
