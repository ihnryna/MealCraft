package org.l5g7.mealcraft.authtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.auth.AuthService;
import org.l5g7.mealcraft.app.auth.dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.dto.RegisterUserDto;
import org.l5g7.mealcraft.app.auth.security.JwtService;
import org.l5g7.mealcraft.app.user.*;
import org.l5g7.mealcraft.enums.Role;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private JwtService jwtService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String HASHED_PASSWORD = "hashed_password_123";
    private static final String TEST_TOKEN = "test.jwt.token";

    private User testUser;
    private RegisterUserDto registerDto;
    private LoginUserDto loginDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(HASHED_PASSWORD);
        testUser.setRole(Role.USER);

        registerDto = new RegisterUserDto();
        registerDto.setUsername(TEST_USERNAME);
        registerDto.setEmail(TEST_EMAIL);
        registerDto.setPassword(TEST_PASSWORD);

        loginDto = new LoginUserDto();
        loginDto.setUsernameOrEmail(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);
    }

    @Test
    void register_Success_ReturnsSuccessMessage() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        ArgumentCaptor<UserRequestDto> userCaptor = ArgumentCaptor.forClass(UserRequestDto.class);

        String result = authService.register(registerDto);

        assertEquals("User registered", result);
        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(userService, times(1)).createUser(userCaptor.capture());

        UserRequestDto capturedUser = userCaptor.getValue();
        assertEquals(TEST_USERNAME, capturedUser.username());
        assertEquals(TEST_EMAIL, capturedUser.email());
        assertEquals(TEST_PASSWORD, capturedUser.password());
        assertEquals(Role.USER, capturedUser.role());
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

        assertThrows(EntityAlreadyExistsException.class, () -> {
            authService.register(registerDto);
        });

        verify(userRepository, times(1)).findByEmail(TEST_EMAIL);
        verify(userService, never()).createUser(any(UserRequestDto.class));
    }

    @Test
    void login_WithUsername_Success_ReturnsToken() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(passwordHasher.hashPassword(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(jwtService.generateToken(TEST_USERNAME, Role.USER)).thenReturn(TEST_TOKEN);

        String result = authService.login(loginDto);

        assertEquals(TEST_TOKEN, result);
        verify(userRepository, times(1)).findAll();
        verify(passwordHasher, times(1)).hashPassword(TEST_PASSWORD);
        verify(jwtService, times(1)).generateToken(TEST_USERNAME, Role.USER);
    }

    @Test
    void login_WithEmail_Success_ReturnsToken() {
        loginDto.setUsernameOrEmail(TEST_EMAIL);
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(passwordHasher.hashPassword(TEST_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(jwtService.generateToken(TEST_USERNAME, Role.USER)).thenReturn(TEST_TOKEN);

        String result = authService.login(loginDto);

        assertEquals(TEST_TOKEN, result);
        verify(jwtService, times(1)).generateToken(TEST_USERNAME, Role.USER);
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        assertThrows(EntityDoesNotExistException.class, () -> {
            authService.login(loginDto);
        });

        verify(passwordHasher, never()).hashPassword(any());
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(passwordHasher.hashPassword(TEST_PASSWORD)).thenReturn("wrong_hash");

        assertThrows(ResponseStatusException.class, () -> {
            authService.login(loginDto);
        });

        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void logout_Success_ReturnsTrue() {
        List<User> users = Arrays.asList(testUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        when(userRepository.findAll()).thenReturn(users);
        SecurityContextHolder.setContext(securityContext);

        boolean result = authService.logout();

        assertTrue(result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void logout_NotAuthenticated_ReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        boolean result = authService.logout();

        assertFalse(result);
        verify(userRepository, never()).findAll();
    }

    @Test
    void logout_NoAuthentication_ReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        boolean result = authService.logout();

        assertFalse(result);
        verify(userRepository, never()).findAll();
    }

    @Test
    void logout_UserNotFound_ThrowsException() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(TEST_USERNAME);
        when(userRepository.findAll()).thenReturn(Arrays.asList());
        SecurityContextHolder.setContext(securityContext);

        assertThrows(EntityDoesNotExistException.class, () -> {
            authService.logout();
        });
    }
}