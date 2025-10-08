package org.l5g7.mealcraft.app.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequestDto(
        @NotBlank
        String text,

        @NotNull
        Long userId
) {}
