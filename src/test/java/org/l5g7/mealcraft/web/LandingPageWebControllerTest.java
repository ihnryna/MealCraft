package org.l5g7.mealcraft.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LandingPageWebControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LandingPageWebController controller = new LandingPageWebController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void landingPage_returnsView() throws Exception {
        mockMvc.perform(get("/mealcraft/landing"))
                .andExpect(status().isOk())
                .andExpect(view().name("landing-page"));
    }
}

