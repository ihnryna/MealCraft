package org.l5g7.mealcraft.web;


import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@RequestMapping("/mealcraft/profile")
public class EditProfileWebController {

    private final RestClient internalApiClient;
    private final UserService userService;

    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String TITLE = "title";
    private static final String PROFILE_PAGE = "profile";

    public EditProfileWebController(@Qualifier("internalApiClient") RestClient internalApiClient, UserService userService) {
        this.internalApiClient = internalApiClient;
        this.userService = userService;
    }

    @GetMapping
    public String getEditProfilePage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserResponseDto userDTO = userService.getUserByUsername(auth.getName());

        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/profile-form");
        model.addAttribute(TITLE, "Edit Profile");

        String username = auth.getName();
        model.addAttribute("username", username);
        addShoppingItemsToModel(model, username);

        model.addAttribute("user", userDTO);
        return PROFILE_PAGE;
    }

    private void addShoppingItemsToModel(Model model, String username) {
        ResponseEntity<List<ShoppingItemDto>> response = internalApiClient.get()
                .uri("/shopping-items/getUserShoppingItems/{id}", userService.getUserByUsername(username).id())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<ShoppingItemDto> shoppingItems = response.getBody();
        model.addAttribute("shoppingItems", shoppingItems);
    }

}
