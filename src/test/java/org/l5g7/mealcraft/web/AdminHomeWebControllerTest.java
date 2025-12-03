package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminHomeWebControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AdminHomeWebController controller = new AdminHomeWebController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void showHome_redirectsToUserPage() throws Exception {
        mockMvc.perform(get("/mealcraft/admin/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mealcraft/admin/user"));
    }
}

