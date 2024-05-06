package com.blogapp.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.blogapp.blog.dto.BlogCreateDTO;
import com.blogapp.blog.dto.BlogFullDTO;
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

}
