package org.l5g7.mealcraft.web;


import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

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

        model.addAttribute("user", userDTO); // Add the whole object

        return PROFILE_PAGE;
    }

}
