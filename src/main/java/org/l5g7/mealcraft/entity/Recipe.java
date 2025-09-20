package org.l5g7.mealcraft.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @NotNull
    private int id;

    @NotNull
    private String name;

    @NotNull
    private int defaultUnitId;

    private String imageUrl;

    private int ownerUserId;
}
