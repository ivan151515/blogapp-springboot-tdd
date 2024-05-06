package com.blogapp.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.profile.ProfileUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
public class IntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequestDto authRequestDto = new AuthRequestDto("validPassword", "validUsername");
    private String token;

    @Order(1)
    @Test
    void register() throws JsonProcessingException, Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isCreated());

    }

    @Order(2)
    @Test
    void login() throws JsonProcessingException, Exception {
        var response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").textValue();
    }

    @Order(3)
    @Test
    void noAuthGetMe_unAuthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Order(4)
    @Test
    void getMe() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("profile.id").value(1));

    }

    @Order(5)
    @Test
    void updateProfile() throws Exception {
        mockMvc.perform(put(("/api/auth/me"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(ProfileUpdateDto.builder().bio("new bio").age(22).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("profile.bio").value("new bio"))
                .andExpect(jsonPath("username").value("validUsername"))
                .andExpect(jsonPath("profile.age").value(22));
    }

    @Order(6)
    @Test
    void createBlog() throws Exception {
        var b = new BlogCreateDTO("title", "this is great content right here", true);

        mockMvc.perform(post("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b))
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("username").value("validUsername"))
                .andExpect(jsonPath("title").value("title"))
                .andExpect(jsonPath("createdAt").isNotEmpty())
                .andExpect(jsonPath("user.password").doesNotExist());
    }
}
