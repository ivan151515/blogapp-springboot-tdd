package com.blogapp.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginResponseDTO {

    private final String token;
}
