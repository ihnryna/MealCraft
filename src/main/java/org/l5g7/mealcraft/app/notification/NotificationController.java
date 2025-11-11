package org.l5g7.mealcraft.app.notification;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationResponseDto> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/getUserNotifications/{id}")
    public List<NotificationResponseDto> getUserNotifications(@PathVariable Long id) {
        return notificationService.getUserNotifications(id);
    }

    @PostMapping("/createNotification")
    public void createNotification(@RequestBody @Valid NotificationRequestDto notification) {
        notificationService.create(notification);
    }

    @DeleteMapping("/deleteNotification/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.delete(id);
    }



}
