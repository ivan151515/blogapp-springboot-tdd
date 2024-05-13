package com.blogapp.blog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.blogapp.blog.comments.dto.CommentCreateDTO;
import com.blogapp.blog.comments.dto.CommentDTO;
import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogUpdateDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;
import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.service.BlogService;
import com.blogapp.exception.AppException;
import com.blogapp.exception.Error;
import com.blogapp.user.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                var b1 = BlogsInfoDTO.builder().id(2L).username("user").createdAt(LocalDateTime.now()).title("title")
                                .build();
                var b2 = BlogsInfoDTO.builder().id(1L).username("user2").createdAt(LocalDateTime.now())
                                .title("valid title")
                                .build();
                when(blogService.getBlogs()).thenReturn(List.of(b1, b2));

                mockMvc.perform(get("/api/blogs")
                                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                                .andExpect(jsonPath("blogs[0].username").value("user"))
                                .andExpect(jsonPath("blogs[0].createdAt").exists())
                                .andExpect(jsonPath("blogs").isArray())
                                .andExpect(jsonPath("blogs", org.hamcrest.Matchers.hasSize(2))); // Verify that there
                                                                                                 // are two elements
                                                                                                 // in

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
                                .andExpect(jsonPath("blog.username").value("username"))
                                .andExpect(jsonPath("blog.important").value(true))
                                .andExpect(jsonPath("blog.comments", org.hamcrest.Matchers.hasSize(2))); // Verify that
                                                                                                         // there are
                                                                                                         // two
                // elements in
                verify(blogService).getBlogById(2L);
        }

        @Test
        @WithMockUser
        void getOneBlog_notFound() throws Exception {
                when(blogService.getBlogById(2L)).thenThrow(new AppException(Error.BLOG_NOT_FOUND));

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
                                .andExpect(status().isUnprocessableEntity());
        }

        @WithMockUser(username = "username")
        @Test
        void postBlog_validRequest() throws JsonProcessingException, Exception {
                var b = new BlogCreateDTO("validTitle", RandomString.make(200), true);
                var returnedBlog = new Blog(1L, "validTitle", b.getContent(), true,
                                new User(1L, "username", null, null, null), LocalDateTime.now(), List.of());
                when(blogService.createBlog(any(BlogCreateDTO.class), anyString()))
                                .thenReturn(BlogFullDTO.mapBlogToBlogFullDTO(returnedBlog));

                mockMvc.perform(post("/api/blogs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(b)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("blog.title").value("validTitle"))
                                .andExpect(jsonPath("blog.id").value(1L))
                                .andExpect(jsonPath("blog.username").value("username"));

                verify(blogService).createBlog(any(BlogCreateDTO.class), anyString());
        }

        @Test
        void updateBlogNoAuthUnauthorized() throws Exception {
                mockMvc.perform(put("/api/blogs/1")).andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser()
        void updateBlogUserDoesNotOwntheBlogForbidden() throws Exception {
                when(blogService.updateBlog(any(BlogUpdateDTO.class), anyString(), anyLong()))
                                .thenThrow(new AppException(Error.ACTION_NOT_ALLOWED));

                mockMvc.perform(put("/api/blogs/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new BlogUpdateDTO())))
                                .andExpect(status().isForbidden());
                verify(blogService).updateBlog(any(BlogUpdateDTO.class), anyString(), anyLong());

        }

        @Test
        @WithMockUser
        void updateBlogServiceThrowsNotFound() throws JsonProcessingException, Exception {
                when(blogService.updateBlog(any(BlogUpdateDTO.class), anyString(), anyLong()))
                                .thenThrow(new AppException(Error.BLOG_NOT_FOUND));

                mockMvc.perform(put("/api/blogs/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new BlogUpdateDTO())))
                                .andExpect(status().isNotFound());

                verify(blogService).updateBlog(any(BlogUpdateDTO.class), anyString(), anyLong());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidPutRequest")
        @WithMockUser
        void updateBlogServiceInvalidRequestBody(BlogUpdateDTO blogUpdateDTO)
                        throws JsonProcessingException, Exception {
                mockMvc.perform(put("/api/blogs/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blogUpdateDTO)))
                                .andExpect(status().isUnprocessableEntity());

        }

        @ParameterizedTest
        @WithMockUser
        @MethodSource("provideValidPutRequest")
        void updateBlogValidRequestBodyUpdatesSuccessfully(BlogUpdateDTO blogUpdateDTO)
                        throws JsonProcessingException, Exception {
                var b = new Blog(1L, "validTitle", RandomString.make(200), true,
                                new User(1L, "username", null, null, null), LocalDateTime.now(), List.of());
                when(blogService.updateBlog(any(BlogUpdateDTO.class), anyString(), anyLong()))
                                .thenReturn(BlogFullDTO.mapBlogToBlogFullDTO(b));

                mockMvc.perform(put("/api/blogs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(blogUpdateDTO)));
        }

        @Test
        void addCommentUnauthorized() throws JsonProcessingException, Exception {
                mockMvc.perform(post(("/api/blogs/1"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CommentCreateDTO("new comment"))))
                                .andExpect(status().isUnauthorized());
        }

        @WithMockUser
        @Test
        void addCommentBlogNotFound() throws JsonProcessingException, Exception {
                when(blogService.addComment(any(CommentCreateDTO.class), anyLong(), anyString()))
                                .thenThrow(new AppException(Error.COMMENT_NOT_FOUND));

                mockMvc.perform(post(("/api/blogs/1"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CommentCreateDTO("new comment"))))
                                .andExpect(status().isNotFound());

                verify(blogService).addComment(any(CommentCreateDTO.class), anyLong(), anyString());
        }

        @ParameterizedTest
        @MethodSource("provideInvalidCommentCreateDTO")
        @WithMockUser
        void addCommentInvalidRequestBody(CommentCreateDTO commentCreateDTO) throws JsonProcessingException, Exception {
                mockMvc.perform(post(("/api/blogs/1"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentCreateDTO)))
                                .andExpect(status().isUnprocessableEntity());
        }

        @Test
        @WithMockUser(username = "username")
        void addCommentValidRequest() throws JsonProcessingException, Exception {
                when(blogService.addComment(any(CommentCreateDTO.class), anyLong(), anyString()))
                                .thenReturn(new CommentDTO("new comment", "username", 1l, LocalDateTime.now()));
                mockMvc.perform(post("/api/blogs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new CommentCreateDTO("new commment"))))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("comment.content").value("new comment"))
                                .andExpect(jsonPath("comment.username").value("username"));

                verify(blogService).addComment(any(CommentCreateDTO.class), anyLong(), anyString());
        }

        @Test
        void deleteCommentUnauthenticated() throws Exception {
                mockMvc.perform(delete(("/api/blogs/1/comments/1")))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "username")
        void blogNotFound404() throws Exception {
                doThrow(new AppException(Error.BLOG_NOT_FOUND)).when(blogService).deleteComment(any(), any(), any());

                mockMvc.perform(delete("/api/blogs/1/comments/1")).andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "username")
        void userDoesNotOwnPostBadRequest() throws Exception {
                doThrow(new AppException(Error.ACTION_NOT_ALLOWED)).when(blogService).deleteComment(1L, 1L, "username");
                mockMvc.perform(delete("/api/blogs/1/comments/1")).andExpect(status().isForbidden());

        }

        @Test
        @WithMockUser(username = "username")
        void commentDeletedSuccesfully() throws Exception {
                mockMvc.perform(delete("/api/blogs/1/comments/1")).andExpect(status().isNoContent());
        }

        @Test
        void deleteBlogUnauthenticated() throws Exception {
                mockMvc.perform(delete("/api/blogs/1")).andExpect(status().isUnauthorized());

        }

        @Test
        @WithMockUser
        void deleteBlogNotFound() throws Exception {
                doThrow(new AppException(Error.BLOG_NOT_FOUND)).when(blogService).deleteBlog(anyLong(), anyString());
                mockMvc.perform(delete("/api/blogs/1")).andExpect(status().isNotFound());

        }

        @Test
        @WithMockUser
        void deleteBlogUserNotOwner() throws Exception {
                doThrow(new AppException(Error.ACTION_NOT_ALLOWED)).when(blogService).deleteBlog(anyLong(),
                                anyString());
                mockMvc.perform(delete("/api/blogs/1")).andExpect(status().isForbidden());

        }

        @Test
        @WithMockUser
        void deleteBlogSuccessNoContent() throws Exception {
                mockMvc.perform(delete("/api/blogs/1")).andExpect(status().isNoContent());

        }

        @Test
        void findBlogsByUserUnauthenticated() throws Exception {
                mockMvc.perform(get("/api/blogs/user/1")).andExpect(status().isUnauthorized());

        }

        @Test
        @WithMockUser
        void findBlogsByUserReturnsOk() throws Exception {
                when(blogService.findBlogsByUser(anyLong())).thenReturn(List.of(mock(BlogsInfoDTO.class)));

                mockMvc.perform(get("/api/blogs/user/1")).andExpect(status().isOk());

                verify(blogService).findBlogsByUser(anyLong());
        }

        private static Stream<Arguments> provideInvalidCommentCreateDTO() {
                return Stream.of(
                                Arguments.of(new CommentCreateDTO(RandomString.make(500))),
                                Arguments.of(new CommentCreateDTO("")),
                                Arguments.of(new CommentCreateDTO()),
                                Arguments.of(new CommentCreateDTO("s")));
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

        private static Stream<Arguments> provideInvalidPutRequest() {
                return Stream.of(
                                Arguments.of(new BlogUpdateDTO(RandomString.make(500), null)),
                                Arguments.of(new BlogUpdateDTO("asd", false)));
        }

        private static Stream<Arguments> provideValidPutRequest() {
                return Stream.of(
                                Arguments.of(new BlogUpdateDTO(RandomString.make(150), null)),
                                Arguments.of(new BlogUpdateDTO(RandomString.make(150), true)),
                                Arguments.of(new BlogUpdateDTO(null, false)));
        }
}
