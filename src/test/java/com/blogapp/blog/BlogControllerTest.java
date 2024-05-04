package com.blogapp.blog;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.service.BlogService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {

    @Autowired
    MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private BlogService BlogService;

    @Test
    @WithMockUser
    void getBlogsReturnsListOfBlogs() throws Exception {
        var b1 = Blog.builder().id(2L).content("hellothere").title("title").build();
        var b2 = Blog.builder().id(1L).content("new content").title("valid title").build();
        when(BlogService.getBlogs()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/blogs")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        // .andExpect(jsonPath(null, null));
        // TODO:
        // .andExpect(jsonPath())
    }
}
