package com.blogapp.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class AuthRequestDto {

    @NotBlank
    @Size(min = 8, max = 32)
    private final String password;

    @NotBlank
    @Size(min = 8, max = 32)
    private final String username;
}
