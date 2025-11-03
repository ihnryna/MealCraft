package org.l5g7.mealcraft.app.shoppingItem;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.user.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingItemDto {

    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String name; //aux

    private Long userOwnerId;
    private Long productId;
    private Integer requiredQty;
    private Boolean status;
    private String unitName; //aux


}
