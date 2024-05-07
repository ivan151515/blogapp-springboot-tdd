package com.blogapp.blog.service;

import java.util.List;

import com.blogapp.blog.comments.dto.CommentCreateDTO;
import com.blogapp.blog.comments.dto.CommentDTO;
import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogUpdateDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;

public interface BlogService {

    List<BlogsInfoDTO> getBlogs();

    BlogFullDTO getBlogById(Long id);

    BlogFullDTO createBlog(BlogCreateDTO b, String string);

    BlogFullDTO updateBlog(BlogUpdateDTO blogUpdateDTO, String username, long blogId);

    CommentDTO addComment(CommentCreateDTO commentCreateDTO, Long blogId, String username);

    void deleteComment(Long blogId, Long commentId, String username);

    void deleteBlog(Long id, String username);

}
