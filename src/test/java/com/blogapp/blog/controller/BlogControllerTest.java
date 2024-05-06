package com.blogapp.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.blogapp.blog.comments.dto.CommentDTO;
import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;
import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.service.BlogService;
import com.blogapp.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import net.bytebuddy.utility.RandomString;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private BlogService blogService;

    @Test
    @WithMockUser
    void getBlogsReturnsListOfBlogs() throws Exception {
        var b1 = BlogsInfoDTO.builder().id(2L).username("user").createdAt(LocalDateTime.now()).title("title").build();
        var b2 = BlogsInfoDTO.builder().id(1L).username("user2").createdAt(LocalDateTime.now()).title("valid title")
                .build();
        when(blogService.getBlogs()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user"))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2))); // Verify that there are two elements in

        verify(blogService).getBlogs();
    }

    @Test
    @WithMockUser
    void getOneBlog() throws Exception {
        var c1 = new CommentDTO();
        var c2 = new CommentDTO();
        var mockBlogFullDTO = new BlogFullDTO("title", "username", 2L, true, 2L, LocalDateTime.now(), "content",
                List.of(c1, c2));

        when(blogService.getBlogById(2L)).thenReturn(mockBlogFullDTO);

        mockMvc.perform(get("/api/blogs/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("username"))
                .andExpect(jsonPath("important").value(true))
                .andExpect(jsonPath("comments", org.hamcrest.Matchers.hasSize(2))); // Verify that there are two
                                                                                    // elements in
        verify(blogService).getBlogById(2L);
    }

    @Test
    @WithMockUser
    void getOneBlog_notFound() throws Exception {
        when(blogService.getBlogById(2L)).thenThrow(new EntityNotFoundException("nto found"));

        mockMvc.perform(get("/api/blogs/2"))
                .andExpect(status().isNotFound());

        verify(blogService).getBlogById(2L);
    }

    @Test
    void postBlog_unauthorized() throws Exception {
        mockMvc.perform(post("/api/blogs"))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @ParameterizedTest
    @MethodSource("provideInvalidPostRequest")
    void postBlog_invalidRequestBodyBadRequest(BlogCreateDTO dto) throws JsonProcessingException, Exception {
        mockMvc.perform(post("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "username")
    @Test
    void postBlog_validRequest() throws JsonProcessingException, Exception {
        var b = new BlogCreateDTO("validTitle", RandomString.make(200), true);
        var returnedBlog = new Blog(1L, "validTitle", b.getContent(), true,
                new User(1L, "username", null, null), LocalDateTime.now(), List.of());
        when(blogService.createBlog(any(BlogCreateDTO.class), anyString()))
                .thenReturn(BlogFullDTO.mapBlogToBlogFullDTO(returnedBlog));

        mockMvc.perform(post("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("title").value("validTitle"))
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("username").value("username"));

        verify(blogService).createBlog(any(BlogCreateDTO.class), anyString());
    }

    @Test
    void updateBlogNoAuthUnauthorized() throws Exception {
        mockMvc.perform(put("/api/blogs/1")).andExpect(status().isUnauthorized());
    }

    private static Stream<Arguments> provideInvalidPostRequest() {
        return Stream.of(
                Arguments.of(new BlogCreateDTO(null, null, null)),
                Arguments.of(new BlogCreateDTO("asd", "dad", null)),
                Arguments.of(new BlogCreateDTO("asd", "dad", true)),
                Arguments.of(new BlogCreateDTO("validTitle", RandomString.make(400), true)),
                Arguments.of(new BlogCreateDTO(null, "validUsername", true)),
                Arguments.of(new BlogCreateDTO("validpassword", null, false)));
    }
}
