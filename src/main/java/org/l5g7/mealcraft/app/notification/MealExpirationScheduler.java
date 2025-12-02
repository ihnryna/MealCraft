package org.l5g7.mealcraft.app.notification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class MealExpirationScheduler {

    private final MealExpirationNotificationService expirationService;

    public MealExpirationScheduler(MealExpirationNotificationService expirationService) {
        this.expirationService = expirationService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void notifyTodayExpiringMeals() {
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        expirationService.notifyForDay(today);
    }

}
