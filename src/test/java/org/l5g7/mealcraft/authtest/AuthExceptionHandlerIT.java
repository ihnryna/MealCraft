package org.l5g7.mealcraft.authtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
class AuthExceptionHandlerIT {

    @TestConfiguration
    static class TestCtrlConfig {
        @RestController
        @RequestMapping("/test-auth")
        @Validated
        static class TestAuthController {
            @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
            public String create(@Valid @RequestBody LoginDto dto) {
                return "ok";
            }
        }

        static class LoginDto {
            @NotBlank(message = "username must not be blank")
            public String username;
            @NotBlank(message = "password must not be blank")
            public String password;
        }

    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser()
    void returnsBadRequestWithErrorsArrayOnValidationFailure() throws Exception {
        String body = "{\"username\":\"\",\"password\":\"\"}";

        mockMvc.perform(post("/test-auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").value(anyOf(
                        is("username must not be blank"),
                        is("password must not be blank"))))
                .andExpect(jsonPath("$.errors[1]").value(anyOf(
                        is("username must not be blank"),
                        is("password must not be blank"))));
    }
}
