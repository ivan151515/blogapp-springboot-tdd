package com.blogapp.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;
import com.blogapp.user.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> register(@RequestBody @Valid AuthRequestDto authRequestDto) {
        return new ResponseEntity<String>("", HttpStatus.OK);
    }
}
