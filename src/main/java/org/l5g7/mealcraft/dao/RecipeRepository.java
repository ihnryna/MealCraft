package org.l5g7.mealcraft.dao;

import org.l5g7.mealcraft.entity.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    List<Recipe> findAll();
    Optional<Recipe> findById(Long id);
    void create(Recipe recipe);
    boolean update(Long id, Recipe recipe);
    boolean deleteById(Long id);
}
