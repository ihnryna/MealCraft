package org.l5g7.mealcraft.app.units;

import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.dto.UnitCreateDto;
import org.l5g7.mealcraft.app.units.dto.UnitDto;
import org.l5g7.mealcraft.app.units.dto.UnitUpdateDto;
import org.l5g7.mealcraft.app.units.interfaces.UnitRepository;
import org.l5g7.mealcraft.app.units.interfaces.UnitService;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.l5g7.mealcraft.logging.LogUtils;
import org.l5g7.mealcraft.logging.LogMarker;
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
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            LogUtils.logInfo("Fetching all units");
            return repository.findAll().stream().map(unit -> new UnitDto(unit.getId(), unit.getName())).toList();
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    public UnitDto getUnitById(Long id) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            Unit result = repository.findById(id).orElseThrow(() -> {
                LogUtils.logWarn("Unit not found: " + id, LogMarker.WARN.getMarkerName());
                return new EntityDoesNotExistException("Unit", String.valueOf(id));
            });
            LogUtils.logInfo("Fetched unit by id: " + id);
            return new UnitDto(result.getId(), result.getName());
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    public UnitDto createUnit(UnitCreateDto unit) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            Unit result = repository.save(Unit.builder()
                    .name(unit.getName())
                    .build());
            LogUtils.logInfo("Created unit: " + result.getId() + ", name: " + result.getName());
            return new UnitDto(result.getId(), result.getName());
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    public UnitDto updateUnit(Long id, UnitUpdateDto updatedUnit) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            Unit unit = repository.findById(id).orElseThrow(() -> {
                LogUtils.logWarn("Unit not found for update: " + id, LogMarker.WARN.getMarkerName());
                return new EntityDoesNotExistException("Unit", String.valueOf(id));
            });

            if (updatedUnit.getName() != null) {
                unit.setName(updatedUnit.getName());
                LogUtils.logInfo("Updated unit name for id: " + id);
            }

            Unit savedUnit = repository.save(unit);
            LogUtils.logInfo("Saved updated unit: " + savedUnit.getId());
            return new UnitDto(savedUnit.getId(), savedUnit.getName());
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    public UnitDto patchUnit(Long id, UnitUpdateDto updates) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            LogUtils.logInfo("Patching unit: " + id);
            return updateUnit(id, updates);
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    public void deleteUnit(Long id) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            repository.deleteById(id);
            LogUtils.logInfo("Deleted unit: " + id);
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }
}
