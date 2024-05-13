package com.blogapp.blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.blog.comments.dto.CommentCreateDTO;
import com.blogapp.blog.comments.dto.CommentRestModel;
import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogRestModel;
import com.blogapp.blog.dto.BlogUpdateDTO;
import com.blogapp.blog.dto.MultipleBlogsRestModel;
import com.blogapp.blog.service.BlogService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<MultipleBlogsRestModel> getBlogs() {

        return new ResponseEntity<MultipleBlogsRestModel>(new MultipleBlogsRestModel(blogService.getBlogs()),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogRestModel> getBlog(@PathVariable Long id) {
        return new ResponseEntity<BlogRestModel>(new BlogRestModel(blogService.getBlogById(id)), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BlogRestModel> createBlog(@RequestBody @Valid BlogCreateDTO blogCreateDTO,
            Authentication authentication) {

        var b = blogService.createBlog(blogCreateDTO, authentication.getName());

        return new ResponseEntity<BlogRestModel>(new BlogRestModel(b), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogRestModel> updateBlog(@RequestBody @Valid BlogUpdateDTO blogUpdateDTO,
            @PathVariable Long id,
            Authentication authentication) {

        return new ResponseEntity<BlogRestModel>(
                new BlogRestModel(blogService.updateBlog(blogUpdateDTO, authentication.getName(), id)),
                HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<CommentRestModel> addComment(@RequestBody @Valid CommentCreateDTO commentCreateDTO,
            Authentication authentication, @PathVariable Long id) {
        return new ResponseEntity<CommentRestModel>(
                new CommentRestModel(blogService.addComment(commentCreateDTO, id, authentication.getName())),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{blogId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long blogId, @PathVariable Long commentId,
            Authentication authentication) {
        blogService.deleteComment(blogId, commentId, authentication.getName());
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id, Authentication authentication) {
        blogService.deleteBlog(id, authentication.getName());

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<MultipleBlogsRestModel> findBlogsByUser(@PathVariable Long id) {
        return new ResponseEntity<MultipleBlogsRestModel>(new MultipleBlogsRestModel(blogService.findBlogsByUser(id)),
                HttpStatus.OK);
    }
}
