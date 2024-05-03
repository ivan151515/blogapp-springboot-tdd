package com.blogapp.user.controller;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;
import com.blogapp.user.dto.RegisterResponseDTO;
import com.blogapp.user.dto.UserDTO;
import com.blogapp.user.profile.Profile;
import com.blogapp.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserService userService;

    @MethodSource("provideInvalidAuthRequest")
    @ParameterizedTest
    void invalidUserLogin_badRequest(AuthRequestDto loginDto) throws JsonProcessingException, Exception {
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validLoginRequest_okResponse() throws JsonProcessingException, Exception {
        var loginDto = new AuthRequestDto("validUsername", "validPassword");
        when(userService.login(any(AuthRequestDto.class))).thenReturn(new LoginResponseDTO("token"));
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value("token"));

        // verify(userService)
    }

    @Test
    void loginServiceThrows_badRequest() throws JsonProcessingException, Exception {
        var loginDto = new AuthRequestDto("validUsername", "validPassword");
        when(userService.login(any(AuthRequestDto.class)))
                .thenThrow(new BadCredentialsException("invalid credentials"));
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("token").doesNotExist());

    }

    @MethodSource("provideInvalidAuthRequest")
    @ParameterizedTest
    void invalidUserRegistration_badRequest(AuthRequestDto registerDto) throws JsonProcessingException, Exception {
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenValidRegistration_returnUserDto() throws JsonProcessingException, Exception {
        when(userService.register(any(AuthRequestDto.class))).thenReturn(new RegisterResponseDTO("success"));
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthRequestDto("validPassword", "validUsername"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("message").value("success"));
    }

    @Test
    void whenServiceRegisterThrows_badRequest() throws JsonProcessingException, Exception {
        when(userService.register(any())).thenThrow(new BadCredentialsException("User already exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AuthRequestDto("validPassword", "validUsername"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getLoggedInUserDetails_noUserUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "validUsername")
    void getLoggedInUserDetails_withUserReturnsProfile() throws Exception {
        when(userService.findUserWithProfile(anyString())).thenReturn(new UserDTO("validUsername", new Profile()));
        mockMvc.perform(get("/api/auth/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("validUsername"))
                .andExpect(jsonPath("profile").exists());
        verify(userService).findUserWithProfile("validUsername");
    }

    private static Stream<Arguments> provideInvalidAuthRequest() {
        return Stream.of(
                Arguments.of(new AuthRequestDto(null, null)),
                Arguments.of(new AuthRequestDto("asd", "dad")),
                Arguments.of(new AuthRequestDto(null, "validUsername")),
                Arguments.of(new AuthRequestDto("validpassword", null)));
    }
}
