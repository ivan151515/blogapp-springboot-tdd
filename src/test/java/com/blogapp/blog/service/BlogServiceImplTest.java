package com.blogapp.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blogapp.blog.comments.Comment;
import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.repository.BlogRepository;
import com.blogapp.user.entity.User;
import com.blogapp.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BlogServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    BlogRepository blogRepository;

    @InjectMocks
    BlogServiceImpl blogServiceImpl;

    @Test
    void getBlogsReturnsListOfBlogs() {
        var b = Blog.builder().user(new User(1L, "user1", null, null)).build();
        var b2 = Blog.builder().user(new User(1L, "user2", null, null)).build();
        when(blogRepository.findAll()).thenReturn(List.of(b, b2));

        blogServiceImpl.getBlogs();

        verify(blogRepository).findAll();

    }

    @Test
    void getBlogById_notFoundThrows() {
        when(blogRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> blogServiceImpl.getBlogById(2L));
    }

    @Test
    void getBlogById_returnsBlog() {
        var b = Blog.builder().comments(List.of(Comment.builder().user(new User()).build()))
                .user(new User(1L, "user1", null, null))
                .build();

        when(blogRepository.findById(anyLong())).thenReturn(Optional.of(b));

        var result = blogServiceImpl.getBlogById(2L);

        assertEquals(b.getUser().getId(), result.getUserId());

        verify(blogRepository).findById(2L);
    }
}
