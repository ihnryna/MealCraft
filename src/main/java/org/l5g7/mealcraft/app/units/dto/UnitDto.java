package org.l5g7.mealcraft.app.units.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitDto {

    private Long id;

    @NotNull
    @NotEmpty
    @NotBlank
    private String name;
}
