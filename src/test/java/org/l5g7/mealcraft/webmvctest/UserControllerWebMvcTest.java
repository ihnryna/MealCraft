package org.l5g7.mealcraft.webmvctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.auth.security.JwtCookieFilter;
import org.l5g7.mealcraft.app.user.UserController;
import org.l5g7.mealcraft.app.user.UserRequestDto;
import org.l5g7.mealcraft.app.user.UserResponseDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.l5g7.mealcraft.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = { JwtCookieFilter.class }
        )
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerWebMvcTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUser_returns200_andBody() throws Exception {
        UserResponseDto response = UserResponseDto.builder()
                .id(1L)
                .username("vika")
                .email("vika@mealcraft.org")
                .role(Role.ADMIN)
                .avatarUrl(null)
                .build();

        when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("vika")))
                .andExpect(jsonPath("$.email", is("vika@mealcraft.org")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.avatarUrl", nullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).getUserById(1L);
    }

    @Test
    void getAllUsers_returns200_andJsonArray_withAllFields() throws Exception {
        List<UserResponseDto> list = List.of(
                UserResponseDto.builder()
                        .id(1L)
                        .username("vika")
                        .email("vika@example.com")
                        .role(Role.ADMIN)
                        .avatarUrl(null)
                        .build(),
                UserResponseDto.builder()
                        .id(2L)
                        .username("ira")
                        .email("ira@example.com")
                        .role(Role.USER)
                        .avatarUrl(null)
                        .build()
        );
        when(userService.getAllUsers()).thenReturn(list);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("vika")))
                .andExpect(jsonPath("$[0].email", is("vika@example.com")))
                .andExpect(jsonPath("$[0].role", is("ADMIN")))
                .andExpect(jsonPath("$[0].avatarUrl", nullValue()))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("ira")))
                .andExpect(jsonPath("$[1].email", is("ira@example.com")))
                .andExpect(jsonPath("$[1].role", is("USER")))
                .andExpect(jsonPath("$[1].avatarUrl", nullValue()));

        verify(userService).getAllUsers();
    }

    @Test
    void postUser_withValidPayload_returns200() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .username("vika")
                .email("vika@mealcraft.org")
                .password("vika123")
                .role(Role.ADMIN)
                .avatarUrl(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).createUser(request);
    }

    @Test
    void putUser_withValidPayload_returns200() throws Exception {
        UserRequestDto update = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        verify(userService).updateUser(1L, update);
    }

    @Test
    void patchUser_withValidPayload_returns200() throws Exception {
        UserRequestDto patch = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk());

        verify(userService).patchUser(1L, patch);
    }

    @Test
    void deleteUser_returns200() throws Exception {
        mockMvc.perform(delete("/users/{id}", 5))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(5L);
    }

    @Test
    void postUser_withBlankUsername_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("") // username blank
                .email("vika@mealcraft.org")
                .password("vika123")
                .role(Role.ADMIN)
                .avatarUrl(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void postUser_withBlankEmail_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("vika")
                .email("") // email blank
                .password("vika123")
                .role(Role.ADMIN)
                .avatarUrl(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void postUser_withInvalidEmail_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("vika")
                .email("email") // email not correct
                .password("vika123")
                .role(Role.ADMIN)
                .avatarUrl(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void postUser_withBlankPassword_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("vika")
                .email("vika@mealcraft.org")
                .password("") // password blank
                .role(Role.ADMIN)
                .avatarUrl(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void postUser_withNullRole_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("vika")
                .email("vika@mealcraft.org")
                .password("vika123")
                .role(null) // role null
                .avatarUrl(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void putUser_withBlankUsername_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("") // username blank
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void putUser_withBlankEmail_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("") // email blank
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void putUser_withInvalidEmail_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("ira_at_mealcraft") // email not correct
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void putUser_withBlankPassword_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("") // password blank
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void putUser_withNullRole_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(null) // role null
                .avatarUrl(null)
                .build();

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patchUser_withBlankUsername_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("") // username blank
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patchUser_withBlankEmail_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("") // email blank
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patchUser_withInvalidEmail_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("ira@@mealcraft.org") // email not correct
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patchUser_withBlankPassword_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("") // password blank
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patchUser_withNullRole_returns400() throws Exception {
        UserRequestDto invalid = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(null) // role null
                .avatarUrl(null)
                .build();

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}
