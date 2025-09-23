package org.l5g7.mealcraft.controller;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.entity.Unit;
import org.l5g7.mealcraft.service.UnitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public List<Unit> getAllUnits() {
        return unitService.getAllUnits();
    }

    @GetMapping("/{id}")
    public Unit getUnit(@PathVariable long id) {
        return unitService.getUnitById(id);
    }

    @PostMapping
    public Unit createUnit(@RequestBody @Valid Unit unit) {
        return unitService.createUnit(unit);
    }

    @PutMapping("/{id}")
    public Unit updateUnit(@PathVariable long id, @RequestBody @Valid Unit unit) {
        return unitService.updateUnit(id, unit);
    }

    @PatchMapping("/{id}")
    public Unit patchUnit(@PathVariable long id, @RequestBody Map<String, Object> updates) {
        return unitService.patchUnit(id, updates);
    }

    @DeleteMapping("/{id}")
    public void deleteUnit(@PathVariable long id) {
        unitService.deleteUnit(id);
    }
}
