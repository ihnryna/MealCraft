package org.l5g7.mealcraft.web;

import jakarta.validation.constraints.NotNull;
import org.l5g7.mealcraft.app.notification.NotificationResponseDto;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.units.dto.UnitDto;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminPageWebController {

    private final RestClient internalApiClient;

    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String TITLE = "title";
    private static final String ADMIN_PAGE = "admin-page";


    public AdminPageWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
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


}
