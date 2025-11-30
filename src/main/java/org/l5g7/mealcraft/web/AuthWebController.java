package org.l5g7.mealcraft.web;

import jakarta.servlet.http.HttpServletResponse;
import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.Dto.RegisterUserDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

            // наскільки я розумію, якщо не HttpStatus.OK, то ми випадаємо по Exception (RestClient автоматично кидає Exception при будь-якому статусі >= 400)

            /*Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(auth.getAuthorities());
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                return "redirect:/mealcraft/admin/home";
            } else {*/

            return "redirect:/mealcraft/home";


        } catch (Exception e) {
            model.addAttribute("error", "No such user");
            model.addAttribute("email", email);
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
            model.addAttribute("error", "User with such email or username already exists");
        if (email != null)
            model.addAttribute("email", email);
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam String password2,
                             HttpServletResponse servletResponse,
                             Model model) {
        try {
            if (!password.equals(password2)) {
                model.addAttribute("error", "Passwords do not match");
                model.addAttribute("email", email);
                return "redirect:/mealcraft/register";
            }

            RegisterUserDto userRegDto = new RegisterUserDto();
            userRegDto.setUsername(username);
            userRegDto.setEmail(email);
            userRegDto.setPassword(password);


            ResponseEntity<Void> response = internalApiClient.post()
                    .uri("/auth/register")
                    .body(userRegDto)
                    .retrieve()
                    .toBodilessEntity();

            return "redirect:/mealcraft/login";


        } catch (Exception e) {
            model.addAttribute("error", "User with such email or username already exists");
            model.addAttribute("email", email);
            return "redirect:/mealcraft/register?error=true&email=" + email;
        }
    }
}

