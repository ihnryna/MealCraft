package org.l5g7.mealcraft.app.products;

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
public class ProductDto {

    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private Long defaultUnitId;

    private Long ownerUserId;

    private String imageUrl;

}
