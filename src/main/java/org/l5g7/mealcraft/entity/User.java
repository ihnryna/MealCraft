package org.l5g7.mealcraft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.l5g7.mealcraft.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Entity
public class User {

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String username;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String password;

    private String avatarUrl;
}
