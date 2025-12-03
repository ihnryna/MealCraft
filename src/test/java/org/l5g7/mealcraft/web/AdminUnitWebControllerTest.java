package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class AdminUnitWebControllerTest {

    private MockMvc mockMvc;
    private RestClient internalApiClient;

    @BeforeEach
    void setUp() {
        internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        AdminUnitWebController controller = new AdminUnitWebController(internalApiClient);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void unitsPage_displaysUnitsList() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/units")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        UnitDto.builder().id(1L).name("kg").build(),
                        UnitDto.builder().id(2L).name("liter").build()
                )));

        mockMvc.perform(get("/mealcraft/admin/unit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Units"))
                .andExpect(model().attributeExists("data", "fragmentToLoad"));
    }

    @Test
    void showCreateUnitForm_displaysForm() throws Exception {
        mockMvc.perform(get("/mealcraft/admin/unit/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Create unit"))
                .andExpect(model().attributeExists("unit", "fragmentToLoad"));
    }

    @Test
    void saveUnit_create_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/units")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/admin/unit")
                        .param("name", "New Unit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/unit"));
    }

    @Test
    void saveUnit_update_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.put()
                        .uri("/units/{id}", 1L)
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/mealcraft/admin/unit")
                        .param("id", "1")
                        .param("name", "Updated Unit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/unit"));
    }

    @Test
    void saveUnit_error_showsForm() throws Exception {
        Mockito.when(internalApiClient.post()
                        .uri("/units")
                        .body(Mockito.any())
                        .retrieve()
                        .toBodilessEntity())
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "", null, null, null));

        mockMvc.perform(post("/mealcraft/admin/unit")
                        .param("name", "Bad Unit"))
                .andExpect(status().is(302));
    }

    @Test
    void showEditUnitForm_displaysFormWithData() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/units/{id}", 1L)
                        .retrieve()
                        .body(UnitDto.class))
                .thenReturn(UnitDto.builder().id(1L).name("kg").build());

        mockMvc.perform(get("/mealcraft/admin/unit/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Edit unit"))
                .andExpect(model().attributeExists("unit", "fragmentToLoad"));
    }

    @Test
    void deleteUnit_success_redirectsToList() throws Exception {
        Mockito.when(internalApiClient.delete()
                        .uri("/units/{id}", 1L)
                        .retrieve()
                        .toBodilessEntity())
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/mealcraft/admin/unit/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/unit"));
    }

    @Test
    void deleteUnit_error_showsListWithError() throws Exception {
        Mockito.when(internalApiClient.delete()
                        .uri("/units/{id}", 1L)
                        .retrieve()
                        .toBodilessEntity())
                .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "", null, null, null));

        Mockito.when(internalApiClient.get()
                        .uri("/units")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/mealcraft/admin/unit/delete/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attributeExists("errorMessage", "data"));
    }
}

