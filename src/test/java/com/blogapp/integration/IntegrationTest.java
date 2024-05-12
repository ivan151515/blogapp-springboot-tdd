package com.blogapp.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.blogapp.blog.comments.dto.CommentCreateDTO;
import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogUpdateDTO;
import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.profile.ProfileUpdateDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
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
                .andExpect(jsonPath("user.profile.id").value(1));

    }

    @Order(5)
    @Test
    void updateProfile() throws Exception {
        mockMvc.perform(put(("/api/auth/me"))
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(ProfileUpdateDto.builder().bio("new bio").age(22).build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("user.profile.bio").value("new bio"))
                .andExpect(jsonPath("user.username").value("validUsername"))
                .andExpect(jsonPath("user.profile.age").value(22));
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
                .andExpect(jsonPath("blog.id").value(1))
                .andExpect(jsonPath("blog.username").value("validUsername"))
                .andExpect(jsonPath("blog.title").value("title"))
                .andExpect(jsonPath("blog.createdAt").isNotEmpty())
                .andExpect(jsonPath("blog.user.password").doesNotExist());
    }

    @Order(7)
    @Test
    void getBlogs() throws Exception {
        mockMvc.perform(get("/api/blogs")
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("blogs").isArray())
                .andExpect(jsonPath("blogs", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("blogs[0].id").value(1))
                .andExpect(jsonPath("blogs[0].username").value("validUsername"))
                .andExpect(jsonPath("blogs[0].title").value("title"))
                .andExpect(jsonPath("blogs[0].createdAt").isNotEmpty());
    }

    @Order(8)
    @Test
    void getBlogById() throws Exception {
        mockMvc.perform(get("/api/blogs/1")
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("blog.id").value(1))
                .andExpect(jsonPath("blog.username").value("validUsername"))
                .andExpect(jsonPath("blog.title").value("title"))
                .andExpect(jsonPath("blog.createdAt").isNotEmpty())
                .andExpect(jsonPath("blog.user.password").doesNotExist());
    }

    @Order(9)
    @Test
    void updateBlog() throws Exception {
        mockMvc.perform(put("/api/blogs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BlogUpdateDTO("updated content", true)))
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("blog.content").value("updated content"))
                .andExpect(jsonPath("blog.id").value(1))
                .andExpect(jsonPath("blog.important").value(true))
                .andExpect(jsonPath("blog.username").value("validUsername"));
    }

    @Order(10)
    @Test
    void addComment() throws Exception {
        mockMvc.perform(post("/api/blogs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CommentCreateDTO("new comment!")))
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("comment.content").value("new comment!"))
                .andExpect(jsonPath("comment.id").value(1))
                .andExpect(jsonPath("comment.createdAt").exists())
                .andExpect(jsonPath("comment.username").value("validUsername"));
    }

    @Order(11)
    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(delete("/api/blogs/1/comments/1")
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Order(12)
    @Test
    void getBlogAfterDeletedCommment() throws Exception {
        mockMvc.perform(get("/api/blogs/1")
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("blog.comments", org.hamcrest.Matchers.hasSize(0)));

    }

    @Order(13)
    @Test
    void deleteBlog() throws Exception {
        mockMvc.perform(delete("/api/blogs/1")
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Order(14)
    @Test
    void getBlogAterDeletedNotFound() throws Exception {
        mockMvc.perform(get("/api/blogs/1")
                .header(AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
