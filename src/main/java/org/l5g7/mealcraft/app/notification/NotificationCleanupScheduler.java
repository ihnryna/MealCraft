package org.l5g7.mealcraft.app.notification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationCleanupScheduler {

    private final NotificationServiceImpl notificationService;

    public NotificationCleanupScheduler(NotificationServiceImpl notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void cleanupOldNotificationsDaily() {
        notificationService.cleanupOldNotifications();
    }
}
