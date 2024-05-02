package com.blogapp.user.service;

import org.springframework.stereotype.Service;

import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public LoginResponseDTO login(AuthRequestDto loginDto) {
        // TODO Auto-generated method stub
        return new LoginResponseDTO("TOKEN");
    }

}
