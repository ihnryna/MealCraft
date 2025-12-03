package org.l5g7.mealcraft.app.units;

import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.user.CurrentUserProvider;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "units")
public class UnitServiceImpl implements UnitService {

    private static final String ENTITY_NAME = "Unit";

    private final UnitRepository repository;
    private final ProductRepository productRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository repository,
                           ProductRepository productRepository, CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    @Cacheable(key = "'allUnits'")
    public List<UnitDto> getAllUnits() {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            LogUtils.logInfo("Fetching all units");
            return repository.findAll().stream()
                    .map(unit -> new UnitDto(unit.getId(), unit.getName()))
                    .toList();
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    @Cacheable(key = "#id")
    public UnitDto getUnitById(Long id) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            Unit result = repository.findById(id)
                    .orElseThrow(() -> new EntityDoesNotExistException(
                            ENTITY_NAME,
                            "id",
                            String.valueOf(id)
                    ));

            LogUtils.logInfo("Fetched unit by id: " + id);
            return new UnitDto(result.getId(), result.getName());
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    @CacheEvict(allEntries = true)
    public UnitDto createUnit(UnitCreateDto unitDto) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            String name = unitDto.getName();
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Unit name must not be blank");
            }

            if (repository.existsByNameIgnoreCase(name)) {
                throw new EntityAlreadyExistsException(ENTITY_NAME, "name", name);
            }

            Unit result = repository.save(
                    Unit.builder()
                            .name(name)
                            .build()
            );

            LogUtils.logInfo("Created unit: " + result.getId() + ", name: " + result.getName());
            return new UnitDto(result.getId(), result.getName());
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    @CacheEvict(allEntries = true)
    public UnitDto updateUnit(Long id, UnitUpdateDto updatedUnit) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            Unit unit = repository.findById(id)
                    .orElseThrow(() -> new EntityDoesNotExistException(
                            ENTITY_NAME,
                            "id",
                            String.valueOf(id)
                    ));

            if (updatedUnit.getName() != null) {
                String newName = updatedUnit.getName();

                if (repository.existsByNameIgnoreCaseAndIdNot(newName, id)) {
                    throw new EntityAlreadyExistsException(ENTITY_NAME, "name", newName);
                }

                unit.setName(newName);
                LogUtils.logInfo("Updated unit name for id: " + id);
            }

            Unit savedUnit = repository.save(unit);
            LogUtils.logInfo("Saved updated unit: " + savedUnit.getId());
            return new UnitDto(savedUnit.getId(), savedUnit.getName());
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    @CacheEvict(allEntries = true)
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

    @CacheEvict(allEntries = true)
    public void deleteUnit(Long id) {
        String username = getAuthenticatedUsername();
        LogUtils.logMDC("user", username);
        try {
            Unit unit = repository.findById(id)
                    .orElseThrow(() -> new EntityDoesNotExistException(
                            ENTITY_NAME,
                            "id",
                            String.valueOf(id)
                    ));

            boolean usedInProducts = productRepository.existsByDefaultUnit(unit);

            if (usedInProducts) {
                throw new IllegalArgumentException("Cannot delete unit because it is used by some products");
            }

            repository.delete(unit);
            LogUtils.logInfo("Deleted unit: " + id);
        } finally {
            LogUtils.logRemoveKey("user");
        }
    }

    @Override
    public Unit getOrCreateUnitByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Unit name must not be blank");
        }

        String trimmed = name.trim();

        return repository.findFirstByNameIgnoreCase(trimmed)
                .orElseGet(() -> repository.save(
                        Unit.builder()
                                .name(trimmed)
                                .build()
                ));
    }

    @Override
    public List<UnitDto> searchUnitsByPrefix(String prefix) {
        String p = prefix == null ? "" : prefix.trim();
        if (p.isEmpty()) {
            return List.of();
        }

        List<Unit> units = repository.findAllByNameStartingWithIgnoreCase(p);

        return units.stream()
                .map(u -> UnitDto.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .build())
                .toList();
    }


    private String getAuthenticatedUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getName();
        }
        return "anonymous";
    }
}