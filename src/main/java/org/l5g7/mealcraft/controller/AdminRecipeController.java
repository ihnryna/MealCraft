package org.l5g7.mealcraft.controller;

import org.l5g7.mealcraft.entity.Recipe;
import org.l5g7.mealcraft.service.ExternalRecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
