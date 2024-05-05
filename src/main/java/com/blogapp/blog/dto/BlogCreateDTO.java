package com.blogapp.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BlogCreateDTO {

    @NotBlank
    @Size(min = 4, max = 64)
    private String title;

    @NotBlank
    @Size(min = 8, max = 320)
    private String content;

    @NotNull
    private Boolean important;
}
