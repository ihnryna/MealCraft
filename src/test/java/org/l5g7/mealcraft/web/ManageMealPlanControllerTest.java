package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.mealplan.MealPlanDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.l5g7.mealcraft.enums.Role;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ManageMealPlanControllerTest {

    private MockMvc mockMvc;
    private RestClient internalApiClient;
    private UserService userService;

    @BeforeEach
    void setUp() {
        internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        userService = Mockito.mock(UserService.class);
        ManageMealPlanController controller = new ManageMealPlanController(internalApiClient, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        Authentication auth = new UsernamePasswordAuthenticationToken("testUser", "password");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void showAddMealPlanForm_displaysForm() throws Exception {
        UserResponseDto userDto = new UserResponseDto(1L, "testUser", "test@example.com", Role.USER, null);
        when(userService.getUserByUsername("testUser")).thenReturn(userDto);

        when(internalApiClient.get()
                        .uri("/recipes")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        RecipeDto.builder().id(1L).name("Pasta").build()
                )));

        when(internalApiClient.get()
                        .uri("/shopping-items/getUserShoppingItems/{id}", 1L)
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/mealcraft/meals/add/2025-01-15"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("title", "Plan your meal"))
                .andExpect(model().attributeExists("mealPlan", "mealColors", "recipeList", "username", "shoppingItems"));
    }

    @Test
    void showEditMealPlanForm_displaysFormWithData() throws Exception {
        UserResponseDto userDto = new UserResponseDto(1L, "testUser", "test@example.com", Role.USER, null);
        when(userService.getUserByUsername("testUser")).thenReturn(userDto);

        MealPlanDto mealPlan = MealPlanDto.builder().id(1L).name("My Meal").build();
        when(internalApiClient.get()
                        .uri("/meal-plans/{id}", 1L)
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(mealPlan));

        when(internalApiClient.get()
                        .uri("/recipes")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        when(internalApiClient.get()
                        .uri("/shopping-items/getUserShoppingItems/{id}", 1L)
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/mealcraft/meals/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("mealPlan", "mealColors", "recipeList"));
    }

    @Test
    void saveMealPlan_create_redirectsToHome() throws Exception {
        when(internalApiClient.post()
                        .uri("/meal-plans")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/meals/save")
                        .param("name", "New Meal Plan"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/home"));
    }

    @Test
    void saveMealPlan_update_redirectsToHome() throws Exception {
        when(internalApiClient.put()
                        .uri("/meal-plans/{id}", 1L)
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/meals/save")
                        .param("id", "1")
                        .param("name", "Updated Meal Plan"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/home"));
    }

    @Test
    void deleteMealPlan_redirectsToHome() throws Exception {
        when(internalApiClient.delete()
                        .uri("/meal-plans/{id}", 1L)
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/mealcraft/meals/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/home"));
    }
}

