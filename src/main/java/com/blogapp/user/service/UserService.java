package com.blogapp.user.service;

import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;

public interface UserService {

    LoginResponseDTO login(AuthRequestDto loginDto);
}
