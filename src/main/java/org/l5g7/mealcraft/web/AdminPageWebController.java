package org.l5g7.mealcraft.web;

import jakarta.validation.constraints.NotNull;
import org.l5g7.mealcraft.app.notification.Notification;
import org.l5g7.mealcraft.app.notification.NotificationResponseDto;
import org.l5g7.mealcraft.app.products.Product;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.shoppingItem.ShoppingItemDto;
import org.l5g7.mealcraft.app.units.Entity.Unit;
import org.l5g7.mealcraft.app.units.dto.UnitDto;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminPageWebController {

    private final RestClient internalApiClient;

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
        model.addAttribute("fragmentToLoad", "fragments/users :: content");
        model.addAttribute("title", "Users");
        return "admin-page";
    }

    @GetMapping("/recipe")
    public String recipesPage(Model model) {
        ResponseEntity<List<RecipeDto>> response = internalApiClient.get()
                .uri("/recipes")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<RecipeDto>>() {});

        List<RecipeDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute("fragmentToLoad", "fragments/recipes :: content");
        model.addAttribute("title", "Recipes");
        return "admin-page";
    }

    @GetMapping("/product")
    public String productsPage(Model model) {
        ResponseEntity<List<ProductDto>> response = internalApiClient.get()
                .uri("/products")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductDto>>() {});

        List<ProductDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute("fragmentToLoad", "fragments/products :: content");
        model.addAttribute("title", "Products");
        return "admin-page";
    }

    @GetMapping("/notification")
    public String notificationsPage(Model model) {
        ResponseEntity<List<NotificationResponseDto>> response = internalApiClient.get()
                .uri("/notifications")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<NotificationResponseDto>>() {});

        List<NotificationResponseDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute("fragmentToLoad", "fragments/notifications :: content");
        model.addAttribute("title", "Notifications");
        return "admin-page";
    }

    @GetMapping("/shopping-item")
    public String shoppingItemsPage(Model model) {
        ResponseEntity<List<ShoppingItemDto>> response = internalApiClient.get()
                .uri("/shopping-items")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ShoppingItemDto>>() {});

        List<ShoppingItemDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute("fragmentToLoad", "fragments/shopping-items :: content");
        model.addAttribute("title", "Shopping items");
        return "admin-page";
    }

    @GetMapping("/unit")
    public String unitsPage(Model model) {
        ResponseEntity<List<UnitDto>> response = internalApiClient.get()
                .uri("/units")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UnitDto>>() {});

        List<UnitDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute("fragmentToLoad", "fragments/units :: content");
        model.addAttribute("title", "Units");
        return "admin-page";
    }


}
