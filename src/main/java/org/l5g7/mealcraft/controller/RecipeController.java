package org.l5g7.mealcraft.controller;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.entity.Recipe;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    public RecipeController() {
    }

    private final List<Recipe> recipes = new ArrayList<>();


    @GetMapping("/{id}")
    public Recipe getRecipe(@PathVariable Long id) {
        for (Recipe recipe : recipes) {
            if (recipe.getId().equals(id)) {
                return recipe;
            }
        }
        throw new EntityDoesNotExistException("Recipe", id);

    }

    @PostMapping
    public void createRecipe(@Valid @RequestBody Recipe recipe) {
        recipes.add(recipe);
    }

    @PutMapping("/{id}")
    public void updateRecipe(@PathVariable Long id, @Valid @RequestBody Recipe updatedRecipe) {
        for (Recipe recipe : recipes) {
            if (recipe.getId().equals(id)) {
                recipe.setName(updatedRecipe.getName());
                recipe.setImageUrl(updatedRecipe.getImageUrl());
                recipe.setBaseRecipeId(updatedRecipe.getBaseRecipeId());
                recipe.setOwnerUserId(updatedRecipe.getOwnerUserId());
            }
        }
    }

    @DeleteMapping("/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        recipes.removeIf(recipe -> recipe.getId().equals(id));
    }

}
