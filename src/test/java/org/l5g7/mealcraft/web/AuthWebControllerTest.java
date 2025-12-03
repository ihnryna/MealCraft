package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthWebControllerTest {

    private MockMvc mockMvc;
    private RestClient internalApiClient;

    @BeforeEach
    void setUp() {
        internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        AuthWebController controller = new AuthWebController(internalApiClient);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }




    @Test
    void doLogin_success_redirectsToHome() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/auth/login")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, "token=abc").build());

        mockMvc.perform(post("/mealcraft/login")
                        .param("email", "user@test.com")
                        .param("password", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/home"));
    }

    @Test
    void doLogin_failure_redirectsBackWithError() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/auth/login")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenThrow(new RuntimeException("Bad credentials"));

        mockMvc.perform(post("/mealcraft/login")
                        .param("email", "user@test.com")
                        .param("password", "wrong"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void doRegister_success_redirectsToLogin() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/auth/register")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/register")
                        .param("username", "newuser")
                        .param("email", "new@test.com")
                        .param("password", "pass")
                        .param("password2", "pass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/login"));
    }

    @Test
    void doRegister_passwordMismatch_redirectsBack() throws Exception {
        mockMvc.perform(post("/mealcraft/register")
                        .param("username", "newuser")
                        .param("email", "new@test.com")
                        .param("password", "pass1")
                        .param("password2", "pass2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/register"));
    }

    @Test
    void doLogout_redirectsToLogin() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/auth/logout")
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/mealcraft/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/login"));
    }
}

