package com.blogapp.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blogapp.blog.comments.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
