package org.l5g7.mealcraft.mealcraftstarterexternalrecipes;

public interface RecipeProvider {
    ExternalRecipe getRandomRecipe() throws Exception;
}
