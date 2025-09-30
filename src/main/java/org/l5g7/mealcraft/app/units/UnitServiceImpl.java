package org.l5g7.mealcraft.app.units;

import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.dto.UnitCreateDto;
import org.l5g7.mealcraft.app.units.dto.UnitDto;
import org.l5g7.mealcraft.app.units.dto.UnitUpdateDto;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.l5g7.mealcraft.app.units.UnitService;

import java.util.*;

@Service
public class UnitServiceImpl implements UnitService{

    private final UnitRepository repository;

    @Autowired
    public UnitServiceImpl(UnitRepository repository) {
        this.repository = repository;
    }

    public List<UnitDto> getAllUnits() {
        return repository.findAll().stream().map(unit -> new UnitDto(unit.getId(), unit.getName())).toList();
    }

    public UnitDto getUnitById(Long id) {
        Unit result = repository.findById(id).orElseThrow(() -> new EntityDoesNotExistException("Unit", id));
        return new UnitDto(result.getId(), result.getName());
    }

    public UnitDto createUnit(UnitCreateDto unit) {
        Unit result = repository.create(unit);
        return new UnitDto(result.getId(), result.getName());
    }

    public UnitDto updateUnit(Long id, UnitUpdateDto updatedUnit) {
        if (!repository.existsById(id)) throw  new EntityDoesNotExistException("Unit", id);

        Unit result = repository.update(id, updatedUnit);
        return new UnitDto(result.getId(), result.getName());
    }

    public UnitDto patchUnit(Long id, UnitUpdateDto updates) {
        return updateUnit(id, updates);
    }

    public void deleteUnit(Long id) {
        repository.deleteById(id);
    }
}

