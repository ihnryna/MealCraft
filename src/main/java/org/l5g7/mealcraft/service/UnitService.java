package org.l5g7.mealcraft.service;

import org.l5g7.mealcraft.entity.Unit;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnitService {

    private final Map<Long, Unit> units = new HashMap<>();

    public List<Unit> getAllUnits() {
        return new ArrayList<>(units.values());
    }

    public Unit getUnitById(long id) {
        if (!units.containsKey(id)) {
            throw new EntityDoesNotExistException("Unit", id);
        }
        return units.get(id);
    }

    public Unit createUnit(Unit unit) {
        if (units.containsKey(unit.getId())) {
            throw new EntityAlreadyExistsException("Unit", unit.getId());
        }
        units.put(unit.getId(), unit);
        return unit;
    }

    public Unit updateUnit(long id, Unit updatedUnit) {
        if (!units.containsKey(id)) {
            throw new EntityDoesNotExistException("Unit", id);
        }
        updatedUnit.setId(id); // ensure ID stays the same
        units.put(id, updatedUnit);
        return updatedUnit;
    }

    public Unit patchUnit(long id, Map<String, Object> updates) {
        Unit unit = units.get(id);
        if (unit == null) {
            throw new EntityDoesNotExistException("Unit", id);
        }
        if (updates.containsKey("name")) {
            unit.setName((String) updates.get("name"));
        }
        return unit;
    }

    public void deleteUnit(long id) {
        if (!units.containsKey(id)) {
            throw new EntityDoesNotExistException("Unit", id);
        }
        units.remove(id);
    }
}

