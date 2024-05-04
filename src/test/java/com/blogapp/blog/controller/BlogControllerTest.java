package com.blogapp.blog.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;
import com.blogapp.blog.service.BlogService;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {

    @Autowired
    MockMvc mockMvc;

    // private ObjectMapper objectMapper = new ObjectMapper();

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
    void getOneBlog() {
        when(blogService.getBlogById(2L)).thenReturn(mock(BlogFullDTO.class));

        // TODO:
    }
}
