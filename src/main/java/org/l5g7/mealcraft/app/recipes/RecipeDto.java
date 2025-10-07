package org.l5g7.mealcraft.app.recipes;

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

    @NotNull
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    //private Long ownerUserId;

    private Long baseRecipeId;

    @NotNull
    private LocalDateTime createdAt;

    private String imageUrl;

}