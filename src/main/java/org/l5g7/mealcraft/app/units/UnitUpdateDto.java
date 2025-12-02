package org.l5g7.mealcraft.app.units;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitUpdateDto {

    private Long id;
    private String name;
}
