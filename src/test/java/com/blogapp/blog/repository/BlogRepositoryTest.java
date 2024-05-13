package com.blogapp.blog.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.blogapp.blog.entity.Blog;
import com.blogapp.user.entity.User;
import com.blogapp.user.repository.UserRepository;

@DataJpaTest
public class BlogRepositoryTest {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(123L, "USERNAME", null, null, null);
        user = userRepository.saveAndFlush(user);
        blogRepository.saveAndFlush(Blog.builder().user(user).build());
        blogRepository.saveAndFlush(Blog.builder().user(user).build());
    }

    @Test
    void findBlogsByUserReturnsBlogsByUser() {
        var result = blogRepository.findBlogsByUser(user.getId());

        assertEquals(result.size(), 2);
        assertEquals(result.get(0).getUser().getId(), user.getId());
    }
}
