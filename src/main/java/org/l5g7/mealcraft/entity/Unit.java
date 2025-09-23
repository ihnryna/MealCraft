package org.l5g7.mealcraft.entity;

import jakarta.persistence.*;
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
