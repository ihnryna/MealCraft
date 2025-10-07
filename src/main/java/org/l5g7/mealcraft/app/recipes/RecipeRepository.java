package org.l5g7.mealcraft.app.recipes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository  extends JpaRepository<Recipe, Long> {}
