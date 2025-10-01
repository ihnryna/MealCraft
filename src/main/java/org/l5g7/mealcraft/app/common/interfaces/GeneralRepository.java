package org.l5g7.mealcraft.app.common.interfaces;

import java.util.List;
import java.util.Optional;

public interface GeneralRepository<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean existsById(ID id);
    T create(T createUnit);
    T update(ID id, T updateUnit);
    void deleteById(ID id);
}
