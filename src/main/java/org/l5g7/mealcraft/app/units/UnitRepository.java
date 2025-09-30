package org.l5g7.mealcraft.app.units;

import org.l5g7.mealcraft.app.common.interfaces.GeneralRepository;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.dto.UnitCreateDto;
import org.l5g7.mealcraft.app.units.dto.UnitUpdateDto;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UnitRepository implements GeneralRepository<Unit, Long, UnitCreateDto, UnitUpdateDto> {

    private final List<Unit> units= new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Unit> findAll() {
        return units;
    }

    @Override
    public Optional<Unit> findById(Long aLong) {
        return units.stream().filter(unit -> unit.getId() == aLong).findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return units.stream().anyMatch(unit -> unit.getId() == aLong);
    }

    @Override
    public Unit create(UnitCreateDto entity) {
        Unit newUnit = new Unit(nextId++, entity.getName());
        units.add(newUnit);
        return newUnit;
    }

   @Override
   public Unit update(Long id, UnitUpdateDto entity) {
       return findById(id)
               .map(toUpdate -> {
                   if (entity.getName() != null) {
                       toUpdate.setName(entity.getName());
                   }
                   return toUpdate;
               }).orElse(null);
   }

    @Override
    public void deleteById(Long id) {
        units.removeIf(unit -> unit.getId() == id);
    }
}
