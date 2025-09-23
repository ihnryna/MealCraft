package org.l5g7.mealcraft.service;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.controller.UserController;
import org.l5g7.mealcraft.dto.LoginUserDto;
import org.l5g7.mealcraft.dto.RegisterUserDto;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthService() {
    }

    public String register(@Valid RegisterUserDto username) {
        return "User registered";
    }

    public String login(LoginUserDto username) {
        return "User logged in";
    }

    public String logout(String username) {
        return "User logged out";
    }
}
