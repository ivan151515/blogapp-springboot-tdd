package com.blogapp.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.blogapp.blog.comments.dto.CommentDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class BlogFullDTO {

    private String title;
    private String username;
    private Long userId;
    private Boolean important;
    private Long id;
    private LocalDateTime createdAt;
    private String content;
    private List<CommentDTO> comments;
}
