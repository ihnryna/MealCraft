package org.l5g7.mealcraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto {

    private Long id;

    private Long ownerUserId;

    private Long baseRecipeId;

    @NotBlank
    @NotNull
    private String name;

    @NotNull
    private LocalDateTime createdAt;

    private String imageUrl;

}