package org.l5g7.mealcraft.web;

import jakarta.servlet.http.HttpServletResponse;
import org.l5g7.mealcraft.app.auth.dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.dto.RegisterUserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@RequestMapping("/mealcraft")
public class AuthWebController {

    private static final String ERROR_ATTR = "error";
    private static final String EMAIL_ATTR = "email";

    private final RestClient internalApiClient;

    public AuthWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String error,
                                @RequestParam(required = false) String email,
                                Model model) {
        if (error != null)
            model.addAttribute(ERROR_ATTR, "No such user");
        if (email != null)
            model.addAttribute(EMAIL_ATTR, email);
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpServletResponse servletResponse,
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

            List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null) {
                servletResponse.addHeader(HttpHeaders.SET_COOKIE, setCookies.get(0));
            }

            return "redirect:/mealcraft/home";


        } catch (Exception e) {
            model.addAttribute(ERROR_ATTR, "No such user");
            model.addAttribute(EMAIL_ATTR, email);
            return "redirect:/mealcraft/login?error=true&email=" + email;
        }
    }

    @GetMapping("/logout")
    public String doLogout(HttpServletResponse servletResponse) {
        try {
            ResponseEntity<Void> response = internalApiClient.post()
                    .uri("/auth/logout")
                    .retrieve()
                    .toBodilessEntity();

            List<String> setCookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null) {
                servletResponse.addHeader(HttpHeaders.SET_COOKIE, setCookies.get(0));
            }

            return "redirect:/mealcraft/login";
        } catch (Exception e) {
            return "redirect:/mealcraft/home";
        }
    }

    @GetMapping("/register")
    public String getRegisterUserPage(@RequestParam(required = false) String error,
                                      @RequestParam(required = false) String email,
                                      Model model) {
        if (error != null)
            model.addAttribute(ERROR_ATTR, "User with such email or username already exists");
        if (email != null)
            model.addAttribute(EMAIL_ATTR, email);
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String password2,
                             Model model) {
        try {
            if (!password.equals(password2)) {
                model.addAttribute(ERROR_ATTR, "Passwords do not match");
                model.addAttribute(EMAIL_ATTR, email);
                return "redirect:/mealcraft/register";
            }

            RegisterUserDto userRegDto = new RegisterUserDto();
            userRegDto.setUsername(username);
            userRegDto.setEmail(email);
            userRegDto.setPassword(password);

            internalApiClient
                    .post()
                    .uri("/auth/register")
                    .body(userRegDto)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/mealcraft/login";

        } catch (Exception e) {
            model.addAttribute(ERROR_ATTR, "User with such email or username already exists");
            model.addAttribute(EMAIL_ATTR, email);
            return "redirect:/mealcraft/register?error=true&email=" + email;
        }
    }
}

