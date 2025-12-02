package org.l5g7.mealcraft.app.recipes;

import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.units.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    long countByCreatedAtBetween(Date from, Date to);
    List<Recipe> findAllByBaseRecipe(Recipe baseRecipe);
    List<Recipe> findAllByIngredientsProduct(Product product);
    List<Recipe> findAllByOwnerUserIsNullOrOwnerUser_Id(Long ownerId);
    List<Recipe> findAllByOwnerUserIsNull();
}
