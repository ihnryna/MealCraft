package org.l5g7.mealcraft.app.units;

import java.util.List;

public interface UnitService {
    List<UnitDto> getAllUnits();
    UnitDto getUnitById(Long id);
    UnitDto createUnit(UnitCreateDto unit);
    UnitDto updateUnit(Long id, UnitUpdateDto updatedUnit);
    UnitDto patchUnit(Long id, UnitUpdateDto updates);
    void deleteUnit(Long id);
}
