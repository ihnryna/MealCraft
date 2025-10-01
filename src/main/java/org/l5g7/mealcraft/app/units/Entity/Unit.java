package org.l5g7.mealcraft.app.units.Entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {

    @NotNull
    private long id;

    @NotNull
    @NotEmpty
    @NotBlank
    private String name;
}
