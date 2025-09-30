package org.l5g7.mealcraft.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.l5g7.mealcraft.dto.ProductDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private Long defaultUnitId;

    private Long ownerUserId;

    private String imageUrl;

    public Product(ProductDto productDto) {
        this.id = (productDto.getId()!=null)? productDto.getId():0;
        this.ownerUserId = productDto.getOwnerUserId();
        this.defaultUnitId = productDto.getDefaultUnitId();
        this.name = productDto.getName();
        this.imageUrl = productDto.getImageUrl();
    }

}
