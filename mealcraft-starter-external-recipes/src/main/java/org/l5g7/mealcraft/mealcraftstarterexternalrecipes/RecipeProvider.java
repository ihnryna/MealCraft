package org.l5g7.mealcraft.mealcraftstarterexternalrecipes;
import java.util.NoSuchElementException;

public interface RecipeProvider {
    ExternalRecipe getRandomRecipe() throws NoSuchElementException;
}
