package org.l5g7.mealcraft.app.recipes;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.products.ProductService;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/{id}")
    public RecipeDto getRecipe(@PathVariable Long id) {
       return recipeService.getRecipeById(id);
    }

    @PostMapping
    public void createRecipe(@Valid @RequestBody RecipeDto recipe) {
        recipeService.createRecipe(recipe);
    }

    @PutMapping("/{id}")
    public void updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDto updatedRecipe) {
        recipeService.updateRecipe(id, updatedRecipe);

    }

    @PatchMapping("/{id}")
    public void patchRecipe(@PathVariable Long id, @RequestBody RecipeDto partialUpdate) {
        recipeService.patchRecipe(id, partialUpdate);
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipeById(id);
    }

}
