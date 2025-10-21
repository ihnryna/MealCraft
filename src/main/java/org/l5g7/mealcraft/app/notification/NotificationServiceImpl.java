package org.l5g7.mealcraft.app.notification;

import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.app.user.UserRepository;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.l5g7.mealcraft.logging.LogMarker;
import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<NotificationResponseDto> getUserNotifications(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            LogUtils.logWarn("User not found for notifications: " + userId, LogMarker.WARN.getMarkerName());
            throw new EntityDoesNotExistException("User", String.valueOf(userId));
        }
        LogUtils.logInfo("Fetched notifications for user: " + userId);
        return user.get().getNotifications().stream()
                .map(notification -> new NotificationResponseDto(
                        notification.getId(),
                        notification.getText(),
                        notification.getCreatedAt().toInstant().toString(),
                        notification.getUser().getId()
                ))
                .toList();
    }

    @Override
    public void create(NotificationRequestDto notification) {
        Optional<User> user = userRepository.findById(notification.userId());
        if (user.isEmpty()) {
            LogUtils.logWarn("User not found for notification creation: " + notification.userId(), LogMarker.WARN.getMarkerName());
            throw new EntityDoesNotExistException("User", String.valueOf(notification.userId()));
        }
        Notification entity = Notification.builder()
                .text(notification.text())
                .createdAt(new Date())
                .user(user.get())
                .build();
        notificationRepository.save(entity);
        LogUtils.logInfo("Notification created for user: " + notification.userId());
    }

    @Override
    public void delete(long notificationId) {
        Optional<Notification> notification = notificationRepository.findById(notificationId);
        if (notification.isEmpty()) {
            LogUtils.logWarn("Notification not found for deletion: " + notificationId, LogMarker.WARN.getMarkerName());
            throw new EntityDoesNotExistException("Notification", String.valueOf(notificationId));
        }
        notificationRepository.deleteById(notificationId);
        LogUtils.logInfo("Notification deleted: " + notificationId);
    }
}
