package org.l5g7.mealcraft.app.recipes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController controller;

    @Test
    void getAllRecipes_delegatesToService() {
        RecipeDto recipe1 = RecipeDto.builder().id(1L).name("Recipe 1").build();
        RecipeDto recipe2 = RecipeDto.builder().id(2L).name("Recipe 2").build();
        when(recipeService.getAllRecipes()).thenReturn(Arrays.asList(recipe1, recipe2));

        List<RecipeDto> result = controller.getAllRecipes();

        assertEquals(2, result.size());
        assertEquals("Recipe 1", result.get(0).getName());
        verify(recipeService, times(1)).getAllRecipes();
    }

    @Test
    void getRecipe_delegatesToService() {
        RecipeDto recipe = RecipeDto.builder().id(1L).name("Test Recipe").build();
        when(recipeService.getRecipeById(1L)).thenReturn(recipe);

        RecipeDto result = controller.getRecipe(1L);

        assertEquals(1L, result.getId());
        assertEquals("Test Recipe", result.getName());
        verify(recipeService, times(1)).getRecipeById(1L);
    }

    @Test
    void getRecipesByProducts_delegatesToService() {
        List<String> products = Arrays.asList("Flour", "Sugar");
        RecipeDto recipe = RecipeDto.builder().id(1L).name("Cake").build();
        when(recipeService.getRecipesByProducts(products)).thenReturn(List.of(recipe));

        List<RecipeDto> result = controller.getRecipesByProducts(products);

        assertEquals(1, result.size());
        assertEquals("Cake", result.get(0).getName());
        verify(recipeService, times(1)).getRecipesByProducts(products);
    }

    @Test
    void createRecipe_delegatesToService() {
        RecipeDto recipe = RecipeDto.builder().name("New Recipe").build();

        controller.createRecipe(recipe);

        verify(recipeService, times(1)).createRecipe(recipe);
    }

    @Test
    void updateRecipe_delegatesToService() {
        RecipeDto recipe = RecipeDto.builder().id(1L).name("Updated Recipe").build();

        controller.updateRecipe(1L, recipe);

        verify(recipeService, times(1)).updateRecipe(1L, recipe);
    }

    @Test
    void patchRecipe_delegatesToService() {
        RecipeDto patch = RecipeDto.builder().name("Patched Recipe").build();

        controller.patchRecipe(1L, patch);

        verify(recipeService, times(1)).patchRecipe(1L, patch);
    }

    @Test
    void deleteRecipe_delegatesToService() {
        controller.deleteRecipe(1L);

        verify(recipeService, times(1)).deleteRecipeById(1L);
    }
}

