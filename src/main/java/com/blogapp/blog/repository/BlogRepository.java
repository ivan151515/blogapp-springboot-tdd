package com.blogapp.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blogapp.blog.entity.Blog;

public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("Select b from Blog b JOIN FETCH b.user where b.user.id =:userId")
    List<Blog> findBlogsByUser(Long userId);

}
