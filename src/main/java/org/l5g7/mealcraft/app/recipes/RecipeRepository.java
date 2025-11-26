package org.l5g7.mealcraft.app.recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface RecipeRepository  extends JpaRepository<Recipe, Long> {
    long countByCreatedAtBetween(Date from, Date to);
}
