package com.blogapp.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.blogapp.blog.comments.Comment;
import com.blogapp.blog.comments.dto.CommentCreateDTO;
import com.blogapp.blog.comments.dto.CommentDTO;
import com.blogapp.blog.comments.repository.CommentRepository;
import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogUpdateDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;
import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.repository.BlogRepository;
import com.blogapp.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<BlogsInfoDTO> getBlogs() {
        var blogs = blogRepository.findAll();

        return blogs.stream().map(BlogsInfoDTO::mapBlogsToBlogsInfoDTO).collect(Collectors.toList());
    }

    @Override
    public BlogFullDTO getBlogById(Long id) {
        var blog = blogRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("NOT FOUND"));

        return BlogFullDTO.mapBlogToBlogFullDTO(blog);
    }

    @Override
    public BlogFullDTO createBlog(BlogCreateDTO blogCreateDTO, String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(username + " not found"));

        var b = Blog.builder().content(blogCreateDTO.getContent()).title(blogCreateDTO.getTitle())
                .important(blogCreateDTO.getImportant()).user(user).build();

        var x = blogRepository.saveAndFlush(b);
        return BlogFullDTO.mapBlogToBlogFullDTO(x);
    }

    @Override
    public BlogFullDTO updateBlog(BlogUpdateDTO blogUpdateDTO, String username, long blogId) {
        var b = blogRepository.findById(blogId).orElseThrow(() -> new EntityNotFoundException("not found"));

        if (!b.getUser().getUsername().equals(username)) {
            throw new RuntimeException("action not allowed");
        }

        if (blogUpdateDTO.getImportant() != null) {
            b.setImportant(blogUpdateDTO.getImportant());
        }
        if (blogUpdateDTO.getContent() != null) {
            b.setContent(blogUpdateDTO.getContent());
        }

        return BlogFullDTO.mapBlogToBlogFullDTO(blogRepository.saveAndFlush(b));
    }

    @Override
    public CommentDTO addComment(CommentCreateDTO commentCreateDTO, Long blogId, String username) {
        var blog = blogRepository.findById(blogId).orElseThrow(() -> new EntityNotFoundException("not found"));

        var user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("not found"));

        var c = Comment.builder().blog(blog).content(commentCreateDTO.getContent()).user(user).build();

        return CommentDTO.mappCommentToCommentDTO(commentRepository.saveAndFlush(c));
    }

    @Override
    public void deleteComment(Long blogId, Long commentId, String username) {
        var c = commentRepository.findCommmentByIdandBlogId(commentId, blogId)
                .orElseThrow(() -> new EntityNotFoundException("not found"));

        if (!c.getUser().getUsername().equals(username)) {
            throw new RuntimeException("forbidden");
        }

        commentRepository.delete(c);
    }

    @Override
    public void deleteBlog(Long id, String username) {
        var b = blogRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("not found"));

        if (!b.getUser().getUsername().equals(username)) {
            throw new RuntimeException("not allowed");
        }
        blogRepository.delete(b);
    }

}
