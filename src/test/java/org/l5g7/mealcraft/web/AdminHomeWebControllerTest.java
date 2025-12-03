package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminHomeWebControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RestClient internalApiClient = Mockito.mock(RestClient.class, RETURNS_DEEP_STUBS);
        AdminHomeWebController controller = new AdminHomeWebController(internalApiClient);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void showHome_redirectsToUserPage() throws Exception {
        mockMvc.perform(get("/mealcraft/admin/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/user"));
    }
}

