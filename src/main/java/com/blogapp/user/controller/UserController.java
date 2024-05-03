package com.blogapp.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;
import com.blogapp.user.dto.RegisterResponseDTO;
import com.blogapp.user.dto.UserDTO;
import com.blogapp.user.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> postMethodName(@RequestBody @Valid AuthRequestDto entity) {
        return new ResponseEntity<LoginResponseDTO>(userService.login(entity), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid AuthRequestDto authRequestDto)
            throws BadRequestException {
        return new ResponseEntity<RegisterResponseDTO>(userService.register(authRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getLoggedInUser(Authentication authentication) {
        return new ResponseEntity<UserDTO>(userService.findUserWithProfile(authentication.getName()), HttpStatus.OK);
    }
}
