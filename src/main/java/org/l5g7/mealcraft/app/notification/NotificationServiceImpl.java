package org.l5g7.mealcraft.app.notification;

import org.l5g7.mealcraft.app.user.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    public NotificationServiceImpl(NotificationRepository notificationRepository, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void createNotification(User user, String text) {
        Notification entity = Notification.builder()
                .text(text)
                .createdAt(new Date())
                .user(user)
                .build();

        notificationRepository.save(entity);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(user.getEmail());
            msg.setSubject("MealCraft");
            msg.setText(text);
            mailSender.send(msg);
        }
    }

    public void cleanupOldNotifications() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        Date border = cal.getTime();

        notificationRepository.deleteByCreatedAtBefore(border);
    }
}
