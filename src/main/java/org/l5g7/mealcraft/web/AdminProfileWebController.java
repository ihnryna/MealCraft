package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminProfileWebController {

    private final UserService userService;

    public AdminProfileWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String adminProfilePage(Model model, Authentication authentication) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserResponseDto userDTO = userService.getUserByUsername(auth.getName());

        model.addAttribute("user", userDTO);
        model.addAttribute("title", "Edit profile");
        model.addAttribute("fragmentToLoad", "fragments/profile-form :: content");
        return "admin-page";

    }
}
