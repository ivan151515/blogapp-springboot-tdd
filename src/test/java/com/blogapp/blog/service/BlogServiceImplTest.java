package com.blogapp.blog.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.repository.BlogRepository;
import com.blogapp.user.entity.User;
import com.blogapp.user.repository.UserRepository;

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
}
