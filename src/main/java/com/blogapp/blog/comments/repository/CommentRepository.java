package com.blogapp.blog.comments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blogapp.blog.comments.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("Select c from Comment c JOIN FETCH c.user where c.id = :id and c.blog.id = :blogId")
    Optional<Comment> findCommmentByIdandBlogId(Long id, Long blogId);

}
