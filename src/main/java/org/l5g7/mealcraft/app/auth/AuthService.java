package org.l5g7.mealcraft.app.auth;

import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import org.l5g7.mealcraft.app.auth.Dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.Dto.RegisterUserDto;
import org.l5g7.mealcraft.app.auth.security.JwtService;
import org.l5g7.mealcraft.app.user.*;
import org.l5g7.mealcraft.enums.Role;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.l5g7.mealcraft.logging.LogMarker;
import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordHasher passwordHasher;

    @Autowired
    private JwtService jwtService;

    public String register(@Valid RegisterUserDto username) {
        LogUtils.logInfo("Registering user: " + username);
        if (userRepository.findByEmail(username.getEmail()).isPresent()) {
            LogUtils.logWarn("Username already exists: " + username.getUsername());
            throw new EntityAlreadyExistsException("Username", username.getUsername());
        }

        userService.createUser(new UserRequestDto(
                username.getUsername(),
                username.getEmail(),
                username.getPassword(),
                Role.USER,
                null
        ));

        LogUtils.logInfo("User registered: " + username);
        return "User registered";
    }

    public String login(LoginUserDto loginUser) {
        LogUtils.logInfo("Login attempt for: " + loginUser.getUsernameOrEmail());
        User user = userRepository.findAll().stream()
                .filter(usr -> usr.getUsername().equals(loginUser.getUsernameOrEmail()) || usr.getEmail().equals(loginUser.getUsernameOrEmail()))
                .findFirst()
                .orElseThrow(() -> {
                    LogUtils.logWarn("User not found: " + loginUser.getUsernameOrEmail(), LogMarker.WARN.getMarkerName());
                    return new RuntimeException("User not found");
                });

        if (!passwordHasher.hashPassword(loginUser.getPassword()).equals(user.getPassword())) {
            LogUtils.logWarn("Invalid password for user: " + loginUser.getUsernameOrEmail());
            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        LogUtils.logInfo("Login successful for: " + user.getUsername());
        return token;
    }

    public boolean logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.isAuthenticated())) {
            return false;
        }

        var username = auth.getName();

        LogUtils.logInfo("Logging out user: " + username);
        if (userRepository.findAll().stream().filter(u-> u.getUsername().equals(username)).findFirst().isEmpty()) {
            LogUtils.logWarn("User not found for logout: " + username, LogMarker.WARN.getMarkerName());
            throw new EntityDoesNotExistException("User", username);
        }
        return true;
    }
}