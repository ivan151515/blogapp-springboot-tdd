package com.blogapp.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogapp.blog.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {

}
