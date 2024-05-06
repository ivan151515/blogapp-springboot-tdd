package com.blogapp.blog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;
import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public List<BlogsInfoDTO> getBlogs() {

        return blogService.getBlogs();
    }

    @GetMapping("/{id}")
    public BlogFullDTO getBlog(@PathVariable Long id) {
        return blogService.getBlogById(id);
    }

    @PostMapping
    public ResponseEntity<Blog> createBlog(@RequestBody @Valid BlogCreateDTO blogCreateDTO,
            Authentication authentication) {

        var b = blogService.createBlog(blogCreateDTO, authentication.getName());

        return new ResponseEntity<Blog>(b, HttpStatus.CREATED);
    }
}
