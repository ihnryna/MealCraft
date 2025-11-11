package org.l5g7.mealcraft.app.user;

import lombok.Builder;
import org.l5g7.mealcraft.enums.Role;

@Builder
public record UserResponseDto(

        Long id,

        String username,

        String email,

        Role role,

        String avatarUrl
) {}
