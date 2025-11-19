package org.l5g7.mealcraft.app.user;

import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.recipes.RecipeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/recipes")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminRecipeController {

    private final RecipeService recipeService;

    public AdminRecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/random")
    public RecipeDto getRandomRecipe() throws Exception {
        return recipeService.getRandomRecipe();
    }

}
