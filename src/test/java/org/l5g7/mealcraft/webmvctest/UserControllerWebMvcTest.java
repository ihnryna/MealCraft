package org.l5g7.mealcraft.webmvctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

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

        mockMvc.perform(get("/users/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("vika")))
                .andExpect(jsonPath("$.email", is("vika@mealcraft.org")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.avatarUrl", nullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).getUserById(1L);
    }

    @Test
    void getAllUsers_returns200_andArray() throws Exception {
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

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("vika")))
                .andExpect(jsonPath("$[0].email", is("vika@example.com")))
                .andExpect(jsonPath("$[0].role", is("ADMIN")))
                .andExpect(jsonPath("$[0].avatarUrl", nullValue()))
                .andExpect(jsonPath("$.password").doesNotExist())

                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("ira")))
                .andExpect(jsonPath("$[1].email", is("ira@example.com")))
                .andExpect(jsonPath("$[1].role", is("USER")))
                .andExpect(jsonPath("$[1].avatarUrl", nullValue()))
                .andExpect(jsonPath("$.password").doesNotExist());


        verify(userService).getAllUsers();
    }

    @Test
    void postUser_withValidPayload_returns200_andDelegatesToService() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .username("vika")
                .email("vika@mealcraft.org")
                .password("vika123")
                .role(Role.ADMIN)
                .avatarUrl(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).createUser(request);
    }

    @Test
    void putUser_withValidPayload_returns200_andDelegatesToService() throws Exception {
        UserRequestDto update = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        verify(userService).updateUser(1L, update);
    }

    @Test
    void patchUser_withValidPayload_returns200_andDelegatesToService() throws Exception {
        UserRequestDto patchDto = UserRequestDto.builder()
                .username("ira")
                .email("ira@mealcraft.org")
                .password("ira_pass")
                .role(Role.USER)
                .avatarUrl(null)
                .build();

        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk());

        verify(userService).patchUser(1L, patchDto);
    }

    @Test
    void deleteUser_returns200_andDelegatesToService() throws Exception {
        mockMvc.perform(delete("/users/{id}", 5)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(5L);
    }

    @ParameterizedTest
    @MethodSource("invalidPostRequests")
    void postUser_withInvalidData_returns400_andDoesNotCallService(UserRequestDto invalid) throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @ParameterizedTest
    @MethodSource("invalidPutRequests")
    void putUser_withInvalidData_returns400_andDoesNotCallService(UserRequestDto invalid) throws Exception {
        mockMvc.perform(put("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @ParameterizedTest
    @MethodSource("invalidPatchRequests")
    void patchUser_withInvalidData_returns400_andDoesNotCallService(UserRequestDto invalid) throws Exception {
        mockMvc.perform(patch("/users/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    static Stream<Arguments> invalidPostRequests() {
        return Stream.of(
                Arguments.of(UserRequestDto.builder()
                        .username("")
                        .email("vika@mealcraft.org")
                        .password("vika123")
                        .role(Role.ADMIN)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("vika")
                        .email("")
                        .password("vika123")
                        .role(Role.ADMIN)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("vika")
                        .email("invalid-email")
                        .password("vika123")
                        .role(Role.ADMIN)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("vika")
                        .email("vika@mealcraft.org")
                        .password("")
                        .role(Role.ADMIN)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("vika")
                        .email("vika@mealcraft.org")
                        .password("vika123")
                        .role(null)
                        .build())
        );
    }

    static Stream<Arguments> invalidPutRequests() {
        return Stream.of(
                Arguments.of(UserRequestDto.builder()
                        .username("")
                        .email("ira@mealcraft.org")
                        .password("ira123")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("")
                        .password("ira123")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("ira_at_mealcraft")
                        .password("ira123")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("ira@mealcraft.org")
                        .password("")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("ira@mealcraft.org")
                        .password("ira123")
                        .role(null)
                        .build())
        );
    }

    static Stream<Arguments> invalidPatchRequests() {
        return Stream.of(
                Arguments.of(UserRequestDto.builder()
                        .username("")
                        .email("ira@mealcraft.org")
                        .password("ira123")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("")
                        .password("ira123")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("ira@@mealcraft.org")
                        .password("ira123")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("ira@mealcraft.org")
                        .password("")
                        .role(Role.USER)
                        .build()),
                Arguments.of(UserRequestDto.builder()
                        .username("ira")
                        .email("ira@mealcraft.org")
                        .password("ira123")
                        .role(null)
                        .build())
        );
    }
}