package org.l5g7.mealcraft.app.recipes;

import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.ExternalRecipeService;

import java.util.List;

public interface RecipeService {

    List<RecipeDto> getAllRecipes();
    RecipeDto getRecipeById(Long id);
    void createRecipe(RecipeDto recipe);
    void updateRecipe(Long id, RecipeDto recipeDto);
    void patchRecipe(Long id, RecipeDto patch);
    void deleteRecipeById(Long id);
    RecipeDto getRandomRecipe() throws Exception;

}

