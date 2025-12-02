package org.l5g7.mealcraft.authtest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.auth.AuthController;
import org.l5g7.mealcraft.app.auth.AuthService;
import org.l5g7.mealcraft.app.auth.dto.LoginUserDto;
import org.l5g7.mealcraft.app.auth.dto.RegisterUserDto;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthController authController;

    private static final String AUTH_TOKEN_NAME = "authToken";
    private static final String TEST_TOKEN = "test.jwt.token";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authController, "authToken", AUTH_TOKEN_NAME);
    }

    @Test
    void register_Success() {
        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername(TEST_USERNAME);
        registerDto.setEmail(TEST_EMAIL);
        registerDto.setPassword(TEST_PASSWORD);

        when(authService.register(any(RegisterUserDto.class))).thenReturn("User registered");

        String result = authController.register(registerDto);

        assertEquals("User registered", result);
        verify(authService, times(1)).register(registerDto);
    }

    @Test
    void login_Success_ReturnsCookieAndOkResponse() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setUsernameOrEmail(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);

        when(authService.login(any(LoginUserDto.class))).thenReturn(TEST_TOKEN);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        ResponseEntity<String> result = authController.login(loginDto, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Logged in successfully", result.getBody());

        verify(response, times(1)).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        assertEquals(AUTH_TOKEN_NAME, capturedCookie.getName());
        assertEquals(TEST_TOKEN, capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(24 * 60 * 60, capturedCookie.getMaxAge());
    }

    @Test
    void login_UserNotFound_ReturnsUnauthorized() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setUsernameOrEmail(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);

        when(authService.login(any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("User not found"));

        ResponseEntity<String> result = authController.login(loginDto, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertTrue(result.getBody().contains("Wrong login or password"));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    void login_InvalidPassword_ReturnsUnauthorized() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setUsernameOrEmail(TEST_USERNAME);
        loginDto.setPassword("wrongpassword");

        when(authService.login(any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("Invalid password"));

        ResponseEntity<String> result = authController.login(loginDto, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertTrue(result.getBody().contains("Wrong login or password"));
    }

    @Test
    void login_InternalError_ReturnsUnauthorized() {
        LoginUserDto loginDto = new LoginUserDto();
        loginDto.setUsernameOrEmail(TEST_USERNAME);
        loginDto.setPassword(TEST_PASSWORD);

        when(authService.login(any(LoginUserDto.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        ResponseEntity<String> result = authController.login(loginDto, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertTrue(result.getBody().contains("Internal error"));
    }

    @Test
    void logout_Success_ClearsCookieAndReturnsOk() {
        when(authService.logout()).thenReturn(true);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        ResponseEntity<String> result = authController.logout(response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Logged out successfully", result.getBody());

        verify(response, times(1)).addCookie(cookieCaptor.capture());
        Cookie capturedCookie = cookieCaptor.getValue();

        assertEquals(AUTH_TOKEN_NAME, capturedCookie.getName());
        assertEquals("", capturedCookie.getValue());
        assertTrue(capturedCookie.isHttpOnly());
        assertEquals("/", capturedCookie.getPath());
        assertEquals(0, capturedCookie.getMaxAge());
    }

    @Test
    void logout_Failure_ReturnsBadRequest() {
        when(authService.logout()).thenReturn(false);

        ResponseEntity<String> result = authController.logout(response);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Logout failed", result.getBody());
        verify(response, never()).addCookie(any(Cookie.class));
    }
}