package org.l5g7.mealcraft.app.common.interfaces;

import java.util.List;
import java.util.Optional;

public interface GeneralRepository<T, ID, CreateDto, UpdateDto> {
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    T create(CreateDto createDto);
    T update(ID id, UpdateDto updateDto);
    void deleteById(ID id);
}
