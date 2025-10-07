package org.l5g7.mealcraft.controller;

import org.l5g7.mealcraft.app.recipes.Recipe;
import org.l5g7.mealcraft.app.recipes.ExternalRecipeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/recipes")
public class AdminRecipeController {

    private final ExternalRecipeService externalRecipeService;

    public AdminRecipeController(ExternalRecipeService externalRecipeService) {
        this.externalRecipeService = externalRecipeService;
    }

    @PostMapping("/import-random")
    public Recipe importRecipe() throws Exception {
        return externalRecipeService.getRandomRecipe();
    }

}
