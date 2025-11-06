package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

@Controller
@RequestMapping("/mealcraft")
public class AuthWebController {

    private final RestClient internalApiClient;

    public AuthWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String email,
                                Model model) {
        if (error != null)
            model.addAttribute("error", "No such user");
        if (email != null)
            model.addAttribute("email", email);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          Model model) {
        try {
            LoginUserDto dto = new LoginUserDto();
            dto.setUsernameOrEmail(email);
            dto.setPassword(password);

            ResponseEntity<Void> response = internalApiClient.post()
                    .uri("/auth/login")
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();

            // наскільки я розумію, якщо не HttpStatus.OK, то ми випадаємо по Exception (RestClient автоматично кидає Exception при будь-якому статусі >= 400)
            return "redirect:/mealcraft/home";

        } catch (Exception e) {
            model.addAttribute("error", "No such user");
            model.addAttribute("email", email);
            return "redirect:/mealcraft/login?error=true&email=" + email;
        }
    }
}

