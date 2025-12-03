package org.l5g7.mealcraft.app.recipes;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.ExternalRecipe;
import org.l5g7.mealcraft.mealcraftstarterexternalrecipes.ExternalRecipeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final ExternalRecipeService externalRecipeService;

    public RecipeController(RecipeService recipeService, ExternalRecipeService externalRecipeService) {
        this.recipeService = recipeService;
        this.externalRecipeService = externalRecipeService;
    }

    @GetMapping
    public List<RecipeDto> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public RecipeDto getRecipe(@PathVariable Long id) {
       return recipeService.getRecipeById(id);
    }

    @GetMapping("/search")
    public List<RecipeDto> getRecipesByProducts(@RequestParam List<String> products) {
        return recipeService.getRecipesByProducts(products);
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

    @GetMapping("/external/random")
    public RecipeDto getExternalRandom() {
        ExternalRecipe external = externalRecipeService.getRandomRecipe();
        return ExternalRecipeParser.toRecipeDto(external);
    }

    @PostMapping("/import")
    public void importRecipe(@RequestBody RecipeDto dto) {
        recipeService.importRecipe(dto);
    }
}
