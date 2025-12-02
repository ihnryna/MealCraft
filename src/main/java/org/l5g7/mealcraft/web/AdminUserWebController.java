package org.l5g7.mealcraft.web;

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
public class AdminUserWebController {

    private final RestClient internalApiClient;

    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String TITLE = "title";
    private static final String ADMIN_PAGE = "admin-page";

    public AdminUserWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/user")
    public String usersPage(Model model) {
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
}
