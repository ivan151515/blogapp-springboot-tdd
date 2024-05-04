package com.blogapp.blog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.blog.entity.Blog;
import com.blogapp.blog.service.BlogService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public List<Blog> getBlogs() {

        return List.of();
    }
}
