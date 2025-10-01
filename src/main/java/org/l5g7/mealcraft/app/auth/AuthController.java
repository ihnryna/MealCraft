package org.l5g7.mealcraft.app.auth;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.Dto.RegisterUserDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@Valid  @RequestBody RegisterUserDto registerUserDto) {
        return authService.register(registerUserDto);
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginUserDto loginUserDto) {
        return authService.login(loginUserDto);
    }

    @PostMapping("/logout")
    public String logout(@RequestBody String username) {
        return authService.logout(username);
    }
}
