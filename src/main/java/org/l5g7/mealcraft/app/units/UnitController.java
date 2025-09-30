package org.l5g7.mealcraft.app.units;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.dto.UnitCreateDto;
import org.l5g7.mealcraft.app.units.dto.UnitDto;
import org.l5g7.mealcraft.app.units.dto.UnitUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/units")
public class UnitController {

    private final UnitServiceImpl unitServiceImpl;

    @Autowired
    public UnitController(UnitServiceImpl unitServiceImpl) {
        this.unitServiceImpl = unitServiceImpl;
    }

    @GetMapping
    public List<UnitDto> getAllUnits() {
        return unitServiceImpl.getAllUnits();
    }

    @GetMapping("/{id}")
    public UnitDto getUnit(@PathVariable long id) {
        return unitServiceImpl.getUnitById(id);
    }

    @PostMapping
    public UnitDto createUnit(@RequestBody @Valid UnitCreateDto unit) {
        return unitServiceImpl.createUnit(unit);
    }

    @PutMapping("/{id}")
    public UnitDto updateUnit(@PathVariable long id, @RequestBody @Valid UnitUpdateDto unit) {
        return unitServiceImpl.updateUnit(id, unit);
    }

    @PatchMapping("/{id}")
    public UnitDto patchUnit(@PathVariable long id, @Valid UnitUpdateDto updates) {
        return unitServiceImpl.patchUnit(id, updates);
    }

    @DeleteMapping("/{id}")
    public void deleteUnit(@PathVariable long id) {
        unitServiceImpl.deleteUnit(id);
    }
}
