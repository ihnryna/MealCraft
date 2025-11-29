package org.l5g7.mealcraft.web;

import jakarta.validation.constraints.NotNull;
import org.l5g7.mealcraft.app.notification.NotificationResponseDto;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.statistics.DailyStats;
import org.l5g7.mealcraft.app.statistics.StatisticsService;
import org.l5g7.mealcraft.app.units.dto.UnitCreateDto;
import org.l5g7.mealcraft.app.units.dto.UnitDto;
import org.l5g7.mealcraft.app.units.dto.UnitUpdateDto;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminPageWebController {

    private final RestClient internalApiClient;
    private final StatisticsService statisticsService;

    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String TITLE = "title";
    private static final String ADMIN_PAGE = "admin-page";


    public AdminPageWebController(@Qualifier("internalApiClient") RestClient internalApiClient, StatisticsService statisticsService) {
        this.internalApiClient = internalApiClient;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/home")
    public String showHome(Model model) {
        return loadUsers(model);
    }

    @GetMapping("/user")
    public String usersPage(Model model) {
        return loadUsers(model);
    }

    @NotNull
    private String loadUsers(Model model) {
        ResponseEntity<List<UserResponseDto>> response = internalApiClient.get()
                .uri("/users")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UserResponseDto>>() {});

        List<UserResponseDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/users :: content");
        model.addAttribute(TITLE, "Users");
        return ADMIN_PAGE;
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

        model.addAttribute("recipe", recipe);
        model.addAttribute("title", "Create recipe");
        model.addAttribute("fragmentToLoad", "fragments/recipe-form :: content");

        return ADMIN_PAGE;
    }

    @GetMapping("/recipe/new-from/{id}")
    public String showCreateBasedOnRecipeForm(@PathVariable Long id, Model model) {

        RecipeDto base = internalApiClient
                .get()
                .uri("/recipes/{id}", id)
                .retrieve()
                .body(RecipeDto.class);

        if (base == null) {
            return "redirect:/mealcraft/admin/recipe";
        }

        base.setBaseRecipeId(base.getId());
        base.setId(null);

        model.addAttribute("recipe", base);
        model.addAttribute("title", "Create recipe based on " + base.getName());
        model.addAttribute("fragmentToLoad", "fragments/recipe-form :: content");

        return ADMIN_PAGE;
    }

    @GetMapping("/recipe/edit/{id}")
    public String showEditRecipeForm(@PathVariable Long id, Model model) {

        RecipeDto recipe = internalApiClient
                .get()
                .uri("/recipes/{id}", id)
                .retrieve()
                .body(RecipeDto.class);

        model.addAttribute("recipe", recipe);
        model.addAttribute("title", "Edit recipe");
        model.addAttribute("fragmentToLoad", "fragments/recipe-form :: content");

        return ADMIN_PAGE;
    }

    @PostMapping("/recipe")
    public String saveRecipe(@ModelAttribute("recipe") RecipeDto recipeDto,
                             Model model) {

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
                        .uri("/recipes/{id}", recipeDto.getId())
                        .body(recipeDto)
                        .retrieve()
                        .toBodilessEntity();
            }

            return "redirect:/mealcraft/admin/recipe";

        } catch (RestClientResponseException ex) {

            String message;
            String body = ex.getResponseBodyAsString();

            if (body != null && !body.isBlank()) {
                message = body;
            } else {
                message = "Failed to save recipe: " + ex.getStatusCode();
            }

            model.addAttribute("recipe", recipeDto);
            model.addAttribute(TITLE, title);
            model.addAttribute(FRAGMENT_TO_LOAD, "fragments/recipe-form :: content");
            model.addAttribute("errorMessage", message);

            return ADMIN_PAGE;
        }
    }

    @GetMapping("/recipe/delete/{id}")
    public String deleteRecipe(@PathVariable Long id) {

        internalApiClient
                .delete()
                .uri("/recipes/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/mealcraft/admin/recipe";
    }

    @GetMapping("/recipe/view/{id}")
    public String viewRecipe(@PathVariable Long id, Model model) {
        RecipeDto recipe = internalApiClient
                .get()
                .uri("/recipes/{id}", id)
                .retrieve()
                .body(RecipeDto.class);

        model.addAttribute("recipe", recipe);
        model.addAttribute("title", "Recipe details");
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

    @GetMapping("/product")
    public String productsPage(Model model) {
        ResponseEntity<List<ProductDto>> response = internalApiClient.get()
                .uri("/products")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductDto>>() {});

        List<ProductDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/products :: content");
        model.addAttribute(TITLE, "Products");
        return ADMIN_PAGE;
    }

    @GetMapping("/product/new")
    public String showCreateProductForm(Model model) {
        ProductDto product = new ProductDto();

        ResponseEntity<List<UnitDto>> unitsResponse = internalApiClient
                .get()
                .uri("/units")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UnitDto>>() {
                });

        List<UnitDto> units = unitsResponse.getBody();

        model.addAttribute("product", product);
        model.addAttribute("units", units);
        model.addAttribute("title", "Create product");
        model.addAttribute("fragmentToLoad", "fragments/product-form :: content");

        return "admin-page";
    }

    @PostMapping("/product")
    public String saveProduct(@ModelAttribute("product") ProductDto productDto,
                              Model model) {

        try {
            if (productDto.getId() == null) {
                internalApiClient
                        .post()
                        .uri("/products")
                        .body(productDto)
                        .retrieve()
                        .toBodilessEntity();
            } else {
                internalApiClient
                        .put()
                        .uri("/products/{id}", productDto.getId())
                        .body(productDto)
                        .retrieve()
                        .toBodilessEntity();
            }
            return "redirect:/mealcraft/admin/product";

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                List<UnitDto> units = internalApiClient
                        .get()
                        .uri("/units")
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<UnitDto>>() {
                        });

                model.addAttribute("product", productDto);
                model.addAttribute("units", units);
                model.addAttribute("title", productDto.getId() == null ? "Create product" : "Edit product");
                model.addAttribute("fragmentToLoad", "fragments/product-form :: content");

                model.addAttribute("errorMessage", "Product with this name already exists");

                return "admin-page";
            }
            throw e;
        }
    }

    @GetMapping("/product/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {

        ProductDto product = internalApiClient
                .get()
                .uri("/products/{id}", id)
                .retrieve()
                .body(ProductDto.class);

        List<UnitDto> units = internalApiClient
                .get()
                .uri("/units")
                .retrieve()
                .body(new ParameterizedTypeReference<List<UnitDto>>() {
                });

        model.addAttribute("product", product);
        model.addAttribute("units", units);
        model.addAttribute("title", "Edit product");
        model.addAttribute("fragmentToLoad", "fragments/product-form :: content");

        return "admin-page";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        try {
            internalApiClient
                    .delete()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/mealcraft/admin/product";

        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();
            ResponseEntity<List<ProductDto>> response = internalApiClient
                    .get()
                    .uri("/products")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<ProductDto>>() {});

            List<ProductDto> data = response.getBody();

            model.addAttribute("data", data);
            model.addAttribute("errorMessage", message);
            model.addAttribute(FRAGMENT_TO_LOAD, "fragments/products :: content");
            model.addAttribute(TITLE, "Products");

            return ADMIN_PAGE;
        }
    }


    @GetMapping("/notification")
    public String notificationsPage(Model model) {
        ResponseEntity<List<NotificationResponseDto>> response = internalApiClient.get()
                .uri("/notifications")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<NotificationResponseDto>>() {});

        List<NotificationResponseDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/notifications :: content");
        model.addAttribute(TITLE, "Notifications");
        return ADMIN_PAGE;
    }

    @GetMapping("/shopping-item")
    public String shoppingItemsPage(Model model) {
        ResponseEntity<List<ShoppingItemDto>> response = internalApiClient.get()
                .uri("/shopping-items")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ShoppingItemDto>>() {});

        List<ShoppingItemDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/shopping-items :: content");
        model.addAttribute(TITLE, "Shopping items");
        return ADMIN_PAGE;
    }

    @GetMapping("/unit")
    public String unitsPage(Model model) {
        ResponseEntity<List<UnitDto>> response = internalApiClient.get()
                .uri("/units")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UnitDto>>() {});

        List<UnitDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/units :: content");
        model.addAttribute(TITLE, "Units");
        return ADMIN_PAGE;
    }

    @GetMapping("/unit/new")
    public String showCreateUnitForm(Model model) {
        UnitDto unit = new UnitDto();

        model.addAttribute("unit", unit);
        model.addAttribute("title", "Create unit");
        model.addAttribute("fragmentToLoad", "fragments/unit-form :: content");

        return "admin-page";
    }

    @PostMapping("/unit")
    public String saveUnit(@ModelAttribute("unit") UnitDto unitDto,
                           Model model) {

        try {
            if (unitDto.getId() == null) {
                UnitCreateDto createDto = UnitCreateDto.builder()
                        .name(unitDto.getName())
                        .build();

                internalApiClient
                        .post()
                        .uri("/units")
                        .body(createDto)
                        .retrieve()
                        .toBodilessEntity();

            } else {
                UnitUpdateDto updateDto = UnitUpdateDto.builder()
                        .id(unitDto.getId())
                        .name(unitDto.getName())
                        .build();

                internalApiClient
                        .put()
                        .uri("/units/{id}", unitDto.getId())
                        .body(updateDto)
                        .retrieve()
                        .toBodilessEntity();
            }

            return "redirect:/mealcraft/admin/unit";

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                model.addAttribute("unit", unitDto);
                model.addAttribute("title", unitDto.getId() == null ? "Create unit" : "Edit unit");
                model.addAttribute("fragmentToLoad", "fragments/unit-form :: content");
                model.addAttribute("errorMessage", "Unit with this name already exists");

                return "admin-page";
            }

            throw e;
        }
    }

    @GetMapping("/unit/delete/{id}")
    public String deleteUnit(@PathVariable Long id, Model model) {
        try {
            internalApiClient
                    .delete()
                    .uri("/units/{id}", id)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/mealcraft/admin/unit";

        } catch (HttpClientErrorException e) {

            ResponseEntity<List<UnitDto>> response = internalApiClient.get()
                    .uri("/units")
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<UnitDto>>() {});

            List<UnitDto> data = response.getBody();

            model.addAttribute("data", data);
            model.addAttribute("errorMessage",
                    "This unit cannot be deleted because it is used by existing products.");
            model.addAttribute(FRAGMENT_TO_LOAD, "fragments/units :: content");
            model.addAttribute(TITLE, "Units");

            return ADMIN_PAGE;
        }
    }


    @GetMapping("/unit/edit/{id}")
    public String showEditUnitForm(@PathVariable Long id, Model model) {

        UnitDto unit = internalApiClient
                .get()
                .uri("/units/{id}", id)
                .retrieve()
                .body(UnitDto.class);

        model.addAttribute("unit", unit);
        model.addAttribute("title", "Edit unit");
        model.addAttribute("fragmentToLoad", "fragments/unit-form :: content");

        return "admin-page";
    }

    @GetMapping("/stats")
    public String showYesterdayStats(Model model) {
        DailyStats stats = internalApiClient
                .get()
                .uri("/statistics/yesterday")
                .retrieve()
                .body(DailyStats.class);

        if (stats == null) {
            model.addAttribute("data", List.of());
        } else {
            model.addAttribute("data", List.of(stats));
        }

        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/stats :: content");
        model.addAttribute(TITLE, "Yesterday statistics");
        return ADMIN_PAGE;
    }

}