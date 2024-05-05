
package com.blogapp.blog.comments.dto;

import java.time.LocalDateTime;

import com.blogapp.blog.comments.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
public class CommentDTO {

    private String content;
    private String username;
    private Long id;
    private LocalDateTime createdAt;

    public static CommentDTO mappCommentToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .id(comment.getId())
                .username(comment.getUser().getUsername())
                .build();
    }
}
