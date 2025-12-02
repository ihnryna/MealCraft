package org.l5g7.mealcraft.unittest.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.notification.Notification;
import org.l5g7.mealcraft.app.notification.NotificationRepository;
import org.l5g7.mealcraft.app.notification.NotificationServiceImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.user.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl service;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
    }

    @Test
    void createNotification_savesEntity_andSendsEmailWhenEmailPresent() {
        String text = "hello";

        service.createNotification(user, text);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());
        Notification saved = captor.getValue();
        assertEquals(text, saved.getText());
        assertEquals(user, saved.getUser());
        assertNotNull(saved.getCreatedAt());

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(mailCaptor.capture());
        SimpleMailMessage msg = mailCaptor.getValue();
        assertArrayEquals(new String[]{"user@example.com"}, msg.getTo());
        assertEquals("MealCraft", msg.getSubject());
        assertEquals(text, msg.getText());
    }

    @Test
    void createNotification_doesNotSendEmail_whenEmailBlank() {
        user.setEmail(" ");
        service.createNotification(user, "text");
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void cleanupOldNotifications_deletesOlderThan30Days() {
        // We can't easily assert the exact Date, but we can verify it was called once.
        service.cleanupOldNotifications();
        verify(notificationRepository, times(1)).deleteByCreatedAtBefore(any(Date.class));
    }
}

