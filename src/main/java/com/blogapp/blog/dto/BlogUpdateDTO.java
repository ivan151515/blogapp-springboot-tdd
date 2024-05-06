package com.blogapp.blog.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogUpdateDTO {

    @Size(min = 8, max = 320)
    private String content;

    private Boolean important;
}
