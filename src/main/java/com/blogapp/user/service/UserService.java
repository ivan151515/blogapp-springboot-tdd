package com.blogapp.user.service;

import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;
import com.blogapp.user.dto.RegisterResponseDTO;

import jakarta.validation.Valid;

public interface UserService {

    LoginResponseDTO login(AuthRequestDto loginDto);

    RegisterResponseDTO register(@Valid AuthRequestDto authRequestDto);
}
