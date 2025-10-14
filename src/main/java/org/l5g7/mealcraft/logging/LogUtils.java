package org.l5g7.mealcraft.logging;

import org.l5g7.mealcraft.MealCraftApplication;
import org.slf4j.*;

public class LogUtils {
    private static final Logger log = LoggerFactory.getLogger(MealCraftApplication.class);

    public static void logInfo(String message) {
        log.info(message);
    }

    public static void logMDC(String key, String value) {
        MDC.put(key, value);
    }

    public static void logRemoveKey(String key) {
        MDC.remove(key);
    }

    public static void logWarn(String message) {
        log.warn(message);
    }

    public static void logWarn(String message, String warnType) {
        Marker marker = MarkerFactory.getMarker(warnType);
        log.warn(marker, message);
    }

    public static void logError(String message) {
        log.error(message);
    }

    public static void logError(String message, String errorType) {
        Marker marker = MarkerFactory.getMarker(errorType);
        log.error(marker, message);
    }
}
