package com.blogapp.blog.service;

import java.util.List;

import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;

public interface BlogService {

    List<BlogsInfoDTO> getBlogs();

    BlogFullDTO getBlogById(Long id);

    BlogFullDTO createBlog(BlogCreateDTO b, String string);

}
