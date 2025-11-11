package org.l5g7.mealcraft.app.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record NotificationResponseDto(

        Long id,

        String text,

        String createdAt,

        Long userId
) {}
