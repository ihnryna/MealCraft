package org.l5g7.mealcraft.app.notification;

public record NotificationResponseDto(

        Long id,

        String text,

        String createdAt,

        Long userId
) {}
