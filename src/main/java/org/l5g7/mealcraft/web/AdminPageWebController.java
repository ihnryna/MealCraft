package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.shoppingItem.ShoppingItemDto;
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
@RequestMapping("/mealcraft")
public class AdminPageWebController {

    private final RestClient internalApiClient;

    public AdminPageWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/admin-page")
    public String showHome(Model model) {
        model.addAttribute("fragmentToLoad", "fragments/users :: content");
        model.addAttribute("title", "Users");
        return "admin-page";
    }

    @GetMapping("/user")
    public String usersPage(Model model) {
        model.addAttribute("fragmentToLoad", "fragments/users :: content");
        model.addAttribute("title", "Users");
        return "admin-page";
    }

    @GetMapping("/recipe")
    public String recipesPage(Model model) {
        model.addAttribute("fragmentToLoad", "fragments/recipes :: content");
        model.addAttribute("title", "Recipes");
        return "admin-page";
    }

    @GetMapping("/product")
    public String productsPage(Model model) {
        model.addAttribute("fragmentToLoad", "fragments/products :: content");
        model.addAttribute("title", "Products");
        return "admin-page";
    }

    @GetMapping("/notification")
    public String notificationsPage(Model model) {
        model.addAttribute("fragmentToLoad", "fragments/notifications :: content");
        model.addAttribute("title", "Notifications");
        return "admin-page";
    }

    @GetMapping("/shopping-item")
    public String shoppingItemsPage(Model model) {
        model.addAttribute("fragmentToLoad", "fragments/shopping-items :: content");
        model.addAttribute("title", "Shopping items");
        return "admin-page";
    }

    @GetMapping("/unit")
    public String unitsPage(Model model) {
        model.addAttribute("fragmentToLoad", "fragments/units :: content");
        model.addAttribute("title", "Units");
        return "admin-page";
    }


}
