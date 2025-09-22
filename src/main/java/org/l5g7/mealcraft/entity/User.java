package org.l5g7.mealcraft.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.l5g7.mealcraft.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @NotNull
    private Long id;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NonNull
    private Role role;

    @NotBlank
    private String password;

    private String avatarUrl;
}
