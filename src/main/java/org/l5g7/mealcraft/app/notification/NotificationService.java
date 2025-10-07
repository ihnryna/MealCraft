package org.l5g7.mealcraft.app.notification;

import java.util.List;

public interface NotificationService {
   List<NotificationResponseDto> getUserNotifications(long userId);
   void create(NotificationRequestDto notification);
   void delete(long notificationId);
}
