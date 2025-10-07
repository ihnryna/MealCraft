package org.l5g7.mealcraft.app.user;

import org.l5g7.mealcraft.enums.Role;

public record UserResponseDto(

        Long id,

        String username,

        String email,

        Role role,

        String avatarUrl
) {}
