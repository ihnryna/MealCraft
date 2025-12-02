package org.l5g7.mealcraft.app.units;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitDto {

    private Long id;

    @NotBlank
    private String name;
}
