package org.l5g7.mealcraft.app.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByCreatedAtBefore(Date border);
}
