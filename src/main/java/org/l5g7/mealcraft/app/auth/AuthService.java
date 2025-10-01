package org.l5g7.mealcraft.app.auth;

import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.Dto.RegisterUserDto;
import org.l5g7.mealcraft.app.auth.security.JwtService;
import org.l5g7.mealcraft.dao.UserRepository;
import org.l5g7.mealcraft.entity.User;
import org.l5g7.mealcraft.service.PasswordHasher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private JwtService jwtService;

    public String register(@Valid RegisterUserDto username) {
        return "User registered";
    }

    public String login(LoginUserDto loginUser) {
        User user = userRepository.findAll().stream()
                        .filter(usr -> usr.getUsername().equals(loginUser.getUsernameOrEmail()) || usr.getEmail().equals(loginUser.getUsernameOrEmail()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));

        //if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
        if (!passwordHasher.hashPassword(loginUser.getPassword()).equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user.getUsername());

    }

    public String logout(String username) {
        return "User logged out";
    }
}
