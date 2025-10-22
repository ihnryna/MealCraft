package org.l5g7.mealcraft.app.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.Dto.RegisterUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.cookie-name}")
    private String authToken;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@Valid  @RequestBody RegisterUserDto registerUserDto) {
        return authService.register(registerUserDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginUserDto loginUserDto, HttpServletResponse response) {

        String token = authService.login(loginUserDto);

        Cookie cookie = new Cookie(authToken, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged in successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        if(authService.logout()) {
            Cookie authTokenCookie = new Cookie(authToken, "");
            authTokenCookie.setPath("/");
            authTokenCookie.setHttpOnly(true);
            authTokenCookie.setMaxAge(0);
            response.addCookie(authTokenCookie);

            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Logout failed");
    }
}
