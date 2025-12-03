package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.l5g7.mealcraft.enums.Role;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminUserWebControllerTest {

    private MockMvc mockMvc;
    private RestClient internalApiClient;

    @BeforeEach
    void setUp() {
        internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        AdminUserWebController controller = new AdminUserWebController(internalApiClient);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void usersPage_displaysUsersList() throws Exception {
        Mockito.when(internalApiClient.get()
                        .uri("/users")
                        .retrieve()
                        .toEntity(Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(
                        new UserResponseDto(1L, "user1", "user1@test.com", Role.USER, null),
                        new UserResponseDto(2L, "user2", "user2@test.com", Role.ADMIN, null)
                )));

        mockMvc.perform(get("/mealcraft/admin/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"))
                .andExpect(model().attribute("title", "Users"))
                .andExpect(model().attributeExists("data", "fragmentToLoad"));
    }
}

