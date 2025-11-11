package org.l5g7.mealcraft.app.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.l5g7.mealcraft.enums.Role;

@Builder
public record UserRequestDto(

        @NotBlank
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        Role role,

        String avatarUrl
) {}

