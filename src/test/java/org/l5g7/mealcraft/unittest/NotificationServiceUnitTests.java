package org.l5g7.mealcraft.unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.notification.NotificationRepository;
import org.l5g7.mealcraft.app.notification.NotificationServiceImpl;
import org.l5g7.mealcraft.app.user.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

import static org.mockito.Mockito.*;

@SuppressWarnings("java:S5786")
public class NotificationServiceUnitTests {

    private NotificationRepository notificationRepository;
    private JavaMailSender mailSender;
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        notificationRepository = mock(NotificationRepository.class);
        mailSender = mock(JavaMailSender.class);

        notificationService = new NotificationServiceImpl(
                notificationRepository,
                mailSender
        );
    }

    @Test
    void createNotification_withEmail_savesNotificationAndSendsMail() {
        User user = User.builder()
                .id(1L)
                .username("vika")
                .email("vika@mealcraft.org")
                .password("pass")
                .build();

        String text = "Hello from MealCraft";

        notificationService.createNotification(user, text);

        verify(notificationRepository, times(1))
                .save(argThat(entity ->
                        entity.getUser() == user &&
                                text.equals(entity.getText()) &&
                                entity.getCreatedAt() != null
                ));

        verify(mailSender, times(1))
                .send(argThat((SimpleMailMessage msg) ->
                        msg.getTo() != null &&
                                msg.getTo().length == 1 &&
                                "vika@mealcraft.org".equals(msg.getTo()[0]) &&
                                "MealCraft".equals(msg.getSubject()) &&
                                text.equals(msg.getText())
                ));
    }

    @Test
    void createNotification_withoutEmail_savesNotificationButDoesNotSendMail() {
        User user = User.builder()
                .id(1L)
                .username("vika")
                .email(null)
                .password("pass")
                .build();

        String text = "No email";

        notificationService.createNotification(user, text);

        verify(notificationRepository, times(1))
                .save(argThat(entity ->
                        entity.getUser() == user &&
                                text.equals(entity.getText()) &&
                                entity.getCreatedAt() != null
                ));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void createNotification_withBlankEmail_savesNotificationButDoesNotSendMail() {
        User user = User.builder()
                .id(1L)
                .username("vika")
                .email("   ")
                .password("pass")
                .build();

        String text = "Blank email";

        notificationService.createNotification(user, text);

        verify(notificationRepository, times(1))
                .save(argThat(entity ->
                        entity.getUser() == user &&
                                text.equals(entity.getText()) &&
                                entity.getCreatedAt() != null
                ));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void cleanupOldNotifications_callsRepositoryWithSomeDate() {
        notificationService.cleanupOldNotifications();

        verify(notificationRepository, times(1))
                .deleteByCreatedAtBefore(any(Date.class));
    }
}
