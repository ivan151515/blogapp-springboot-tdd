package com.blogapp.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blogapp.blog.comments.Comment;
import com.blogapp.blog.comments.dto.CommentCreateDTO;
import com.blogapp.blog.comments.repository.CommentRepository;
import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogUpdateDTO;
import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.repository.BlogRepository;
import com.blogapp.exception.AppException;
import com.blogapp.user.entity.User;
import com.blogapp.user.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

@ExtendWith(MockitoExtension.class)
public class BlogServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BlogRepository blogRepository;

    @InjectMocks
    BlogServiceImpl blogServiceImpl;

    @Test
    void getBlogsReturnsListOfBlogs() {
        var b = Blog.builder().user(new User(1L, "user1", null, null, null)).build();
        var b2 = Blog.builder().user(new User(1L, "user2", null, null, null)).build();
        when(blogRepository.findAll()).thenReturn(List.of(b, b2));

        blogServiceImpl.getBlogs();

        verify(blogRepository).findAll();

    }

    @Test
    void getBlogById_notFoundThrows() {
        when(blogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> blogServiceImpl.getBlogById(2L));
    }

    @Test
    void getBlogById_returnsBlog() {
        var b = Blog.builder().comments(List.of(Comment.builder().user(new User()).build()))
                .user(new User(1L, "user1", null, null, null))
                .build();

        when(blogRepository.findById(anyLong())).thenReturn(Optional.of(b));

        var result = blogServiceImpl.getBlogById(2L);

        assertEquals(b.getUser().getId(), result.getUserId());

        verify(blogRepository).findById(2L);
    }

    @Test
    void createBlog_userNotFoundThrows() {
        var b = new BlogCreateDTO("validTitle", RandomString.make(150), true);
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> blogServiceImpl.createBlog(b, "username"));

        verify(userRepository).findByUsername("username");
    }

    @Test
    void createBlog_savesBlogInRepository() {
        var b = new BlogCreateDTO("validTitle", RandomString.make(150), true);
        var u = new User(1L, "username", null, null, null);
        var savedBlog = new Blog(1L, b.getTitle(), b.getContent(), b.getImportant(), u, LocalDateTime.now(), List.of());
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(u));
        when(blogRepository.saveAndFlush(any(Blog.class))).thenReturn(savedBlog);
        var result = blogServiceImpl.createBlog(b, "username");

        assertEquals(savedBlog.getId(), result.getId());
        assertEquals(savedBlog.getContent(), result.getContent());
        assertEquals(savedBlog.getUser().getUsername(), result.getUsername());
        verify(blogRepository).saveAndFlush(any(Blog.class));
    }

    @Test
    void updateBlog_blogNotFoundThrowsException() {
        when(blogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> blogServiceImpl.updateBlog(new BlogUpdateDTO(), "username", 2L));
    }

    @Test
    void whenUserDoesntOwnTheBlogThrowBadRequestError() {
        var b = Blog.builder().user(new User(1L, "user1", null, null, null)).build();
        when(blogRepository.findById(anyLong())).thenReturn(Optional.of(b));

        assertThrows(AppException.class, () -> blogServiceImpl.updateBlog(new BlogUpdateDTO(), "username", 2L));
    }

    @ParameterizedTest
    @MethodSource("provideValidUpdateDTO")
    void updatesBlogSuccesfully(BlogUpdateDTO blogUpdateDTO) {
        var b = Blog.builder()
                .title("new title ")
                .content("valid content")
                .important(true)
                .user(new User(1L, "user1", null, null, null))
                .build();
        var blogToReturn = Blog.builder()
                .title("new title ")
                .content(blogUpdateDTO.getContent() != null ? blogUpdateDTO.getContent() : b.getContent())
                .important(blogUpdateDTO.getImportant() != null ? blogUpdateDTO.getImportant() : b.getImportant())
                .user(new User(1L, "user1", null, null, null))
                .build();

        when(blogRepository.findById(anyLong())).thenReturn(Optional.of(b));
        when(blogRepository.saveAndFlush(any(Blog.class))).thenReturn(blogToReturn);
        var result = blogServiceImpl.updateBlog(blogUpdateDTO, "user1", 1L);

        if (blogUpdateDTO.getImportant() != null) {
            assertEquals(blogUpdateDTO.getImportant(), result.getImportant());
        } else {
            assertEquals(b.getImportant(), result.getImportant());
        }
        if (blogUpdateDTO.getContent() != null) {
            assertEquals(blogUpdateDTO.getContent(), result.getContent());
        } else {
            assertEquals(b.getContent(), result.getContent());
        }
        assertEquals(b.getTitle(), result.getTitle());
        verify(blogRepository).saveAndFlush(any(Blog.class));
    }

    @Test
    void blogNotFoundThrows() {
        when(blogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> blogServiceImpl.addComment(new CommentCreateDTO(), 1L, "username"));
    }

    @Test
    void addsCommentSuccessfully() {
        var b = Blog.builder().id(1L).user(new User(1L, "user1", null, null, null)).build();
        var u = new User(1l, "username", null, null, null);
        when(blogRepository.findById(1L)).thenReturn(Optional.of(b));
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(u));
        when(commentRepository.saveAndFlush(any(Comment.class)))
                .thenReturn(
                        Comment.builder().id(1L).user(u).content("new comment").createdAt(LocalDateTime.now()).blog(b)
                                .build());
        var result = blogServiceImpl.addComment(new CommentCreateDTO("new comment"), 1L, "username");

        assertEquals(result.getContent(), "new comment");
        assertEquals(result.getUsername(), "username");

        verify(commentRepository).saveAndFlush(any(Comment.class));
    }

    @Test
    void deleteCommentBlogNotFoundThrowsNotFound() {
        when(commentRepository.findCommmentByIdandBlogId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> blogServiceImpl.deleteComment(1L, 1l, "username"));
    }

    @Test
    void deleteCommentCommentUserNotOwnerThrows() {
        var user = new User(1L, "notuser", null, null, null);
        var c = new Comment(1L, user, null, null, null);
        when(commentRepository.findCommmentByIdandBlogId(anyLong(), anyLong())).thenReturn(Optional.of(c));

        assertThrows(AppException.class,
                () -> blogServiceImpl.deleteComment(1L, 1l, "username"));
    }

    @Test
    void deleteCommentCommentDeletedSuccesfully() {
        var user = new User(1L, "username", null, null, null);
        var c1 = new Comment(1L, user, null, null, null);

        when(commentRepository.findCommmentByIdandBlogId(anyLong(), anyLong())).thenReturn(Optional.of(c1));

        blogServiceImpl.deleteComment(1L, 1L, "username");

        verify(commentRepository).delete(any(Comment.class));

    }

    @Test
    void deleteBlogThrowNotFound() {
        when(blogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> blogServiceImpl.deleteBlog(1L, "username"));

    }

    @Test
    void deleteBlogThrowNotAllowed() {
        var b = Blog.builder().id(1L).user(new User(1L, "user1", null, null, null)).build();

        when(blogRepository.findById(anyLong())).thenReturn(Optional.of(b));

        assertThrows(AppException.class,
                () -> blogServiceImpl.deleteBlog(1L, "username"));

    }

    @Test
    void deleteBlogSuccesfull() {
        var b = Blog.builder().id(1L).user(new User(1L, "user1", null, null, null)).build();

        when(blogRepository.findById(anyLong())).thenReturn(Optional.of(b));

        blogServiceImpl.deleteBlog(1L, "user1");

        verify(blogRepository).delete(any(Blog.class));
    }

    private static Stream<Arguments> provideValidUpdateDTO() {
        return Stream.of(
                Arguments.of(new BlogUpdateDTO(RandomString.make(150), null)),
                Arguments.of(new BlogUpdateDTO(RandomString.make(150), true)),
                Arguments.of(new BlogUpdateDTO(null, false)));
    }
}
