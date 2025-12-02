package org.l5g7.mealcraft.app.recipeingredient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeIngredientDto {

    private Long id;

    @NotNull
    private Long productId;

    private String productName;

    @NotNull
    @Positive
    private Double amount;

    private String unitName;
}