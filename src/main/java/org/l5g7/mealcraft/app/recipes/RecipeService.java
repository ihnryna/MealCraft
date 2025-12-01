package org.l5g7.mealcraft.app.recipes;
import java.util.List;
import java.util.NoSuchElementException;

public interface RecipeService {

    List<RecipeDto> getAllRecipes();
    RecipeDto getRecipeById(Long id);
    void createRecipe(RecipeDto recipe);
    void updateRecipe(Long id, RecipeDto recipeDto);
    void patchRecipe(Long id, RecipeDto patch);
    void deleteRecipeById(Long id);
    RecipeDto getRandomRecipe() throws NoSuchElementException;
    List<RecipeDto> getRecipesByProducts(List<String> products);

}

