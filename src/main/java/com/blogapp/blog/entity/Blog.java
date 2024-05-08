package com.blogapp.blog.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.blogapp.blog.comments.Comment;
import com.blogapp.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "blogs")
@Entity
public class Blog {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private Boolean important;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "blog", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    public void deleteComment(Long commentId, String username) {
        if (comments == null) {
            throw new EntityNotFoundException("not found");
        }

        comments.forEach(c -> {
            if (c.getId() == commentId && !c.getUser().getUsername().equals(username)) {
                throw new RuntimeException("user not owner");
            }
        });
        var size = comments.size();
        setComments(comments.stream().filter(
                c -> c.getId() != commentId || (c.getId() == commentId && !c.getUser().getUsername().equals(username)))
                .toList());

        if (size == comments.size()) {
            throw new EntityNotFoundException("not found");
        }
    }

}
