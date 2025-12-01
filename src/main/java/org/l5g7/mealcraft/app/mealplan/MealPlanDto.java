package org.l5g7.mealcraft.app.mealplan;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.l5g7.mealcraft.enums.MealStatus;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealPlanDto {

    private Long id;

    @NotNull
    private Long userOwnerId;

    @NotNull
    private Long recipeId;

    @NotNull
    private Date planDate;

    @NotNull
    @Min(1)
    private Integer servings;

    @NotNull
    private MealStatus status;

    private String name;

    private String color;
}
