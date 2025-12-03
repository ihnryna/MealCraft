package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.units.UnitDto;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminProductWebControllerTest {

    private MockMvc mockMvc;
    private RestClient internalApiClient;

    @BeforeEach
    void setUp() {
        internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        AdminProductWebController controller = new AdminProductWebController(internalApiClient);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void productsPage_displaysProducts() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/products")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        ProductDto.builder().id(1L).name("Milk").build(),
                        ProductDto.builder().id(2L).name("Bread").build()
                )));

        mockMvc.perform(get("/mealcraft/admin/product"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Products"))
                .andExpect(model().attributeExists("data", "fragmentToLoad"));
    }

    @Test
    void showCreateProductForm_displaysForm() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/units")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        UnitDto.builder().id(1L).name("kg").build()
                )));

        mockMvc.perform(get("/mealcraft/admin/product/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Create product"))
                .andExpect(model().attributeExists("product", "units", "fragmentToLoad"));
    }

    @Test
    void saveProduct_create_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/products")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/admin/product")
                        .param("name", "New Product"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/product"));
    }

    @Test
    void saveProduct_conflict_showsFormWithError() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/products")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenThrow(new HttpClientErrorException(HttpStatus.CONFLICT));

        Mockito.when(internalApiClient.get()
                        .uri("/units")
                        .retrieve()
                        .body(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(List.of(UnitDto.builder().id(1L).name("kg").build()));

        mockMvc.perform(post("/mealcraft/admin/product")
                        .param("name", "Duplicate Product"))
                .andExpect(status().is(302));  }

    @Test
    void saveProduct_update_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.put()
                        .uri("/products/{id}", 1L)
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/admin/product")
                        .param("id", "1")
                        .param("name", "Updated Product"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/product"));
    }

    @Test
    void showEditProductForm_displaysFormWithData() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/products/{id}", 1L)
                        .retrieve()
                        .body(ProductDto.class))
                .thenReturn(ProductDto.builder().id(1L).name("Milk").build());

        Mockito.when(internalApiClient.get()
                        .uri("/units")
                        .retrieve()
                        .body(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(List.of(UnitDto.builder().id(1L).name("kg").build()));

        mockMvc.perform(get("/mealcraft/admin/product/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Edit product"))
                .andExpect(model().attributeExists("product", "units", "fragmentToLoad"));
    }

    @Test
    void deleteProduct_success_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.delete()
                        .uri("/products/{id}", 1L)
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/mealcraft/admin/product/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/product"));
    }

    @Test
    void deleteProduct_error_showsListWithError() throws Exception {
        Mockito.when(internalApiClient.delete()
                        .uri("/products/{id}", 1L)
                        .retrieve()
                        .toBodilessEntity())
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "", null, null, null));

        Mockito.when(internalApiClient.get()
                        .uri("/products")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        ProductDto.builder().id(1L).name("Milk").build()
                )));

        mockMvc.perform(get("/mealcraft/admin/product/delete/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attributeExists("data", "errorMessage"));
    }
}

