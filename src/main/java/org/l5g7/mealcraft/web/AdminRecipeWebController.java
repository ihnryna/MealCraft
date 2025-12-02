package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminRecipeWebController {

    private final RestClient internalApiClient;

    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String TITLE = "title";
    private static final String ADMIN_PAGE = "admin-page";
    private static final String RECIPE = "recipe";
    private static final String REDIRECT_RECIPE_PAGE = "redirect:/mealcraft/admin/recipe";
    private static final String RECIPE_ID_URI        = "/recipes/{id}";
    private static final String RECIPE_FORM_FRAGMENT = "fragments/recipe-form :: content";

    public AdminRecipeWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/recipe")
    public String recipesPage(Model model) {
        ResponseEntity<List<RecipeDto>> response = internalApiClient.get()
                .uri("/recipes")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<RecipeDto>>() {});

        List<RecipeDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/recipes :: content");
        model.addAttribute(TITLE, "Recipes");
        return ADMIN_PAGE;
    }

    @GetMapping("/recipe/new")
    public String showCreateRecipeForm(Model model) {
        RecipeDto recipe = new RecipeDto();

        model.addAttribute(RECIPE, recipe);
        model.addAttribute(TITLE, "Create recipe");
        model.addAttribute(FRAGMENT_TO_LOAD, RECIPE_FORM_FRAGMENT);

        return ADMIN_PAGE;
    }

    @GetMapping("/recipe/new-from/{id}")
    public String showCreateBasedOnRecipeForm(@PathVariable Long id, Model model) {

        RecipeDto base = internalApiClient
                .get()
                .uri(RECIPE_ID_URI, id)
                .retrieve()
                .body(RecipeDto.class);

        if (base == null) {
            return REDIRECT_RECIPE_PAGE;
        }

        base.setBaseRecipeId(base.getId());
        base.setId(null);

        model.addAttribute(RECIPE, base);
        model.addAttribute(TITLE, "Create recipe based on " + base.getName());
        model.addAttribute(FRAGMENT_TO_LOAD, RECIPE_FORM_FRAGMENT);

        return ADMIN_PAGE;
    }

    @GetMapping("/recipe/edit/{id}")
    public String showEditRecipeForm(@PathVariable Long id, Model model) {

        RecipeDto recipe = internalApiClient
                .get()
                .uri(RECIPE_ID_URI, id)
                .retrieve()
                .body(RecipeDto.class);

        model.addAttribute(RECIPE, recipe);
        model.addAttribute(TITLE, "Edit recipe");
        model.addAttribute(FRAGMENT_TO_LOAD, RECIPE_FORM_FRAGMENT);

        return ADMIN_PAGE;
    }

    @PostMapping("/recipe")
    public String saveRecipe(@ModelAttribute("recipe") RecipeDto recipeDto, Model model) {

        String title = (recipeDto.getId() == null)
                ? "Create recipe"
                : "Edit recipe";

        try {
            if (recipeDto.getId() == null) {
                internalApiClient
                        .post()
                        .uri("/recipes")
                        .body(recipeDto)
                        .retrieve()
                        .toBodilessEntity();
            } else {
                internalApiClient
                        .put()
                        .uri(RECIPE_ID_URI, recipeDto.getId())
                        .body(recipeDto)
                        .retrieve()
                        .toBodilessEntity();
            }

            return REDIRECT_RECIPE_PAGE;

        } catch (RestClientResponseException ex) {

            String message;
            String body = ex.getResponseBodyAsString();

            if (!body.isBlank()) {
                message = body;
            } else {
                message = "Failed to save recipe: " + ex.getStatusCode();
            }

            model.addAttribute(RECIPE, recipeDto);
            model.addAttribute(TITLE, title);
            model.addAttribute(FRAGMENT_TO_LOAD, RECIPE_FORM_FRAGMENT);
            model.addAttribute("errorMessage", message);

            return ADMIN_PAGE;
        }
    }

    @GetMapping("/recipe/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {

        internalApiClient
                .delete()
                .uri(RECIPE_ID_URI, id)
                .retrieve()
                .toBodilessEntity();

        return REDIRECT_RECIPE_PAGE;
    }

    @GetMapping("/recipe/view/{id}")
    public String viewRecipe(@PathVariable Long id, Model model) {
        RecipeDto recipe = internalApiClient
                .get()
                .uri(RECIPE_ID_URI, id)
                .retrieve()
                .body(RecipeDto.class);

        model.addAttribute(RECIPE, recipe);
        model.addAttribute(TITLE, "Recipe details");
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/recipe-details :: content");

        return ADMIN_PAGE;
    }

    @GetMapping("/recipe/product-suggestions")
    @ResponseBody
    public List<ProductDto> getProductSuggestions(@RequestParam("query") String query) {

        return internalApiClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/search")
                        .queryParam("prefix", query)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductDto>>() {});
    }
}
