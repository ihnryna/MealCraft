package org.l5g7.mealcraft.logging;

import org.l5g7.mealcraft.MealCraftApplication;
import org.slf4j.*;

public class LogUtils {
    private static final Logger log = LoggerFactory.getLogger(MealCraftApplication.class);

    public static void logInfo() {
        log.info("MEALCRAFT successfully started!");
        MDC.put("user", "iryna");
        try {
            log.info("Start processing request");
            log.info("Request processed successfully");
            Marker CRITICAL = MarkerFactory.getMarker("BIG_BOB");
            log.warn(CRITICAL, "Something critical happened!");

        } finally {
            MDC.remove("user");
        }
        log.warn("Something looks off...");
        log.error("Oops! Critical error!");
    }
}
