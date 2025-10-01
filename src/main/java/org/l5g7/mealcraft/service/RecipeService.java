package org.l5g7.mealcraft.service;

import org.l5g7.mealcraft.dto.RecipeDto;
import org.l5g7.mealcraft.entity.Recipe;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RecipeService {

    /*private final ExternalRecipeService externalRecipeService;

    public RecipeService(ExternalRecipeService externalRecipeService) {
        this.externalRecipeService = externalRecipeService;
    }*/

    List<Recipe> getAllRecipes();
    Recipe getRecipeById(Long id);
    void createRecipe(Recipe recipe);
    void updateRecipe(Long id, RecipeDto recipeDto);
    void patchRecipe(Long id, RecipeDto patch);
    void deleteRecipeById(Long id);

}

