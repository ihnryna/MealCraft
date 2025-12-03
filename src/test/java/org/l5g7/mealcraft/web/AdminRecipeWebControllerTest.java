package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminRecipeWebControllerTest {

    private MockMvc mockMvc;
    private RestClient internalApiClient;

    @BeforeEach
    void setUp() {
        internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        AdminRecipeWebController controller = new AdminRecipeWebController(internalApiClient);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void recipesPage_displaysRecipes() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/recipes")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        RecipeDto.builder().id(1L).name("Pasta").build(),
                        RecipeDto.builder().id(2L).name("Pizza").build()
                )));

        mockMvc.perform(get("/mealcraft/admin/recipe"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Recipes"))
                .andExpect(model().attributeExists("data", "fragmentToLoad"));
    }

    @Test
    void showCreateRecipeForm_displaysForm() throws Exception {
        mockMvc.perform(get("/mealcraft/admin/recipe/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Create recipe"))
                .andExpect(model().attributeExists("recipe", "fragmentToLoad"));
    }

    @Test
    void showCreateBasedOnRecipeForm_displaysFormWithData() throws Exception {
        RecipeDto base = RecipeDto.builder().id(1L).name("Base Recipe").build();

        Mockito.when(internalApiClient.get()
                        .uri("/recipes/{id}", 1L)
                        .retrieve()
                        .body(RecipeDto.class))
                .thenReturn(base);

        mockMvc.perform(get("/mealcraft/admin/recipe/new-from/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attributeExists("recipe", "fragmentToLoad"));
    }

    @Test
    void showEditRecipeForm_displaysFormWithData() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/recipes/{id}", 1L)
                        .retrieve()
                        .body(RecipeDto.class))
                .thenReturn(RecipeDto.builder().id(1L).name("Pasta").build());

        mockMvc.perform(get("/mealcraft/admin/recipe/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Edit recipe"))
                .andExpect(model().attributeExists("recipe", "fragmentToLoad"));
    }

    @Test
    void saveRecipe_create_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/recipes")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/admin/recipe")
                        .param("name", "New Recipe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/recipe"));
    }

    @Test
    void deleteRecipe_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.delete()
                        .uri("/recipes/{id}", 1L)
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/mealcraft/admin/recipe/delete/1"))
                .andExpect(status().is4xxClientError())
                .andExpect(redirectedUrl("/mealcraft/admin/recipe"));
    }

    @Test
    void viewRecipe_displaysRecipeDetails() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/recipes/{id}", 1L)
                        .retrieve()
                        .body(RecipeDto.class))
                .thenReturn(RecipeDto.builder().id(1L).name("Pasta").build());

        mockMvc.perform(get("/mealcraft/admin/recipe/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attributeExists("recipe", "fragmentToLoad"));
    }

    @Test
    void getProductSuggestions_returnsJson() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri(Mockito.anyString())
                        .retrieve()
                        .body(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(List.of(ProductDto.builder().id(1L).name("Milk").build()));

        mockMvc.perform(get("/mealcraft/admin/recipe/product-suggestions").param("query", "Mi"))
                .andExpect(status().isOk());
    }
}

