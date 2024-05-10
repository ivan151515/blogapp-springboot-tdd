package com.blogapp.blog.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.blogapp.blog.comments.dto.CommentDTO;
import com.blogapp.blog.entity.Blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogFullDTO {

        private String title;
        private String username;
        private Long userId;
        private Boolean important;
        private Long id;
        private LocalDateTime createdAt;
        private String content;
        private List<CommentDTO> comments;

        public static BlogFullDTO mapBlogToBlogFullDTO(Blog blog) {
                return BlogFullDTO.builder()
                                .comments(
                                                blog.getComments() != null
                                                                ? blog.getComments().stream().map(
                                                                                CommentDTO::mappCommentToCommentDTO)
                                                                                .collect(Collectors.toList())
                                                                : List.of())
                                .username(blog.getUser().getUsername())
                                .userId(blog.getUser().getId())
                                .title(blog.getTitle())
                                .important(blog.getImportant())
                                .id(blog.getId())
                                .createdAt(blog.getCreatedAt())
                                .content(blog.getContent())
                                .build();

        }
}
