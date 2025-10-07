package org.l5g7.mealcraft.app.units;

import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.dto.UnitCreateDto;
import org.l5g7.mealcraft.app.units.dto.UnitDto;
import org.l5g7.mealcraft.app.units.dto.UnitUpdateDto;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
import org.l5g7.mealcraft.app.units.interfaces.UnitService;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitServiceImpl implements UnitService {

    private final UnitRepository repository;

    @Autowired
    public UnitServiceImpl(UnitRepository repository) {
        this.repository = repository;
    }

    public List<UnitDto> getAllUnits() {
        showUserinfo();
        return repository.findAll().stream().map(unit -> new UnitDto(unit.getId(), unit.getName())).toList();
    }

    private void showUserinfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            System.out.println("Authenticated user: " + username);
        }
    }

    public UnitDto getUnitById(Long id) {
        Unit result = repository.findById(id).orElseThrow(() -> new EntityDoesNotExistException("Unit", id));
        return new UnitDto(result.getId(), result.getName());
    }

    public UnitDto createUnit(UnitCreateDto unit) {
        Unit result = repository.save(Unit.builder()
                        .name(unit.getName())
                        .build());
        return new UnitDto(result.getId(), result.getName());
    }

    public UnitDto updateUnit(Long id, UnitUpdateDto updatedUnit) {
        Unit unit = repository.findById(id).orElseThrow(() -> new EntityDoesNotExistException("Unit", id));

        if (updatedUnit.getName() != null) {
            unit.setName(updatedUnit.getName());
        }

        Unit savedUnit = repository.save(unit);
        return new UnitDto(savedUnit.getId(), savedUnit.getName());
    }

    public UnitDto patchUnit(Long id, UnitUpdateDto updates) {
        return updateUnit(id, updates);
    }

    public void deleteUnit(Long id) {
        repository.deleteById(id);
    }
}

