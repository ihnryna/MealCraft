package org.l5g7.mealcraft.app.shoppingItem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingItemDto {
    @NotNull
    private Long id;

    @NotNull
    private Long shoppingListId;

    @NotNull
    @NotBlank
    private String name;

    private Long productId;
    private Long sourceMealPlanId;
    private Integer requiredQty;
    private boolean status;


}
