package com.blogapp.blog.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.blogapp.blog.comments.Comment;
import com.blogapp.blog.comments.repository.CommentRepository;
import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.repository.BlogRepository;
import com.blogapp.user.entity.User;
import com.blogapp.user.repository.UserRepository;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    UserRepository userRepository;

    private Comment comment;
    private User user;
    private Blog blog;

    @BeforeEach
    void setUp() {
        user = new User(1L, "USERNAME", null, null, null);
        user = userRepository.saveAndFlush(user);
        blog = blogRepository.saveAndFlush(Blog.builder().user(user).build());

        comment = Comment.builder().content("new comment")
                .blog(blog)
                .user(user).build();

        comment = commentRepository.saveAndFlush(comment);
    }

    @Test
    void findCommmentByIdandBlogId() {
        var result = commentRepository.findCommmentByIdandBlogId(comment.getId(), blog.getId());

        System.out.println(result);
        assertNotNull(result.get());
        assertEquals(blog.getId(), result.get().getBlog().getId());
        assertEquals(result.get().getContent(), comment.getContent());
    }
}
