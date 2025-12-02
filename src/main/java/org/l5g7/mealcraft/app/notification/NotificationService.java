package org.l5g7.mealcraft.app.notification;

import org.l5g7.mealcraft.app.user.User;

public interface NotificationService {
    void createNotification(User user, String text);
}
