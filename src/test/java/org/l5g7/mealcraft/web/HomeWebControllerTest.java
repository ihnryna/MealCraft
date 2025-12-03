package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.l5g7.mealcraft.enums.Role;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeWebControllerTest {

    private MockMvc mockMvc;
    private RestClient internalApiClient;
    private UserService userService;

    @BeforeEach
    void setUp() {
        internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        userService = Mockito.mock(UserService.class);
        HomeWebController controller = new HomeWebController(internalApiClient, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void showHome_asAdmin_redirectsToAdminHome() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/mealcraft/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/home"));
    }

    @Test
    void toggleChecked_redirectsToHome() throws Exception {
        when(internalApiClient.patch()
                        .uri("/shopping-items/toggle/{id}", 1L)
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/shopping/toggle").param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/home"));
    }

    @Test
    void setUserColor_setsSessionAndRedirects() throws Exception {
        mockMvc.perform(post("/mealcraft/user/color").param("color", "blue"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/home"));
    }

    @Test
    void showCraftPage_asUser_displaysCraftPage() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        UserResponseDto user = new UserResponseDto(1L,"testUser","testUser@gmail.com", Role.USER, null);
        when(userService.getUserByUsername("testUser")).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/mealcraft/craft"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("username", "testUser"))
                .andExpect(model().attribute("title", "MealCraft — Recipe Craft"));
    }

    @Test
    void showCraftPage_asAdmin_redirectsToAdminHome() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/mealcraft/craft"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/home"));
    }
}

