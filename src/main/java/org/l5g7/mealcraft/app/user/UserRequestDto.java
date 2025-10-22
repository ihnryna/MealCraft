package org.l5g7.mealcraft.app.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.l5g7.mealcraft.enums.Role;

public record UserRequestDto(

        @NotBlank
        String username,

        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotNull
        Role role,

        String avatarUrl
) {}

