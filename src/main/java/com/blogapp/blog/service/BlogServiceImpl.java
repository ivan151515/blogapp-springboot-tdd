package com.blogapp.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.blogapp.blog.dto.BlogFullDTO;
import com.blogapp.blog.dto.BlogsInfoDTO;
import com.blogapp.blog.repository.BlogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    @Override
    public List<BlogsInfoDTO> getBlogs() {
        var blogs = blogRepository.findAll();

        return blogs.stream().map(BlogsInfoDTO::mapBlogsToBlogsInfoDTO).collect(Collectors.toList());
    }

    @Override
    public BlogFullDTO getBlogById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBlogById'");
    }

}
