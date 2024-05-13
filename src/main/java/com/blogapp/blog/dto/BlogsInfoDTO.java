package com.blogapp.blog.dto;

import java.time.LocalDateTime;

import com.blogapp.blog.entity.Blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BlogsInfoDTO {

    private String title;
    private String username;
    private String content;
    private Long id;
    private LocalDateTime createdAt;

    public static BlogsInfoDTO mapBlogsToBlogsInfoDTO(Blog blog) {
        return BlogsInfoDTO
                .builder()
                .username(blog.getUser().getUsername())
                .content(blog.getContent())
                .createdAt(blog.getCreatedAt())
                .id(blog.getId())
                .title(blog.getTitle())
                .build();
    }
}
