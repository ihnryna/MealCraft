package org.l5g7.mealcraft.app.units;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    boolean existsByNameIgnoreCase(String name);
    List<Unit> findAllByNameStartingWithIgnoreCase(String prefix);
    Optional<Unit> findFirstByNameIgnoreCase(String name);
}
