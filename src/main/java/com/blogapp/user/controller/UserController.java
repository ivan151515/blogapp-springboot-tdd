package com.blogapp.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;
import com.blogapp.user.dto.RegisterResponseDTO;
import com.blogapp.user.dto.UserRestModel;
import com.blogapp.user.profile.ProfileUpdateDto;
import com.blogapp.user.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> postMethodName(@RequestBody @Valid AuthRequestDto entity) {
        return new ResponseEntity<LoginResponseDTO>(userService.login(entity), HttpStatus.OK);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid AuthRequestDto authRequestDto)
            throws BadRequestException {
        return new ResponseEntity<RegisterResponseDTO>(userService.register(authRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UserRestModel> getLoggedInUser(Authentication authentication) {
        return new ResponseEntity<UserRestModel>(
                new UserRestModel(userService.findUserWithProfile(authentication.getName())), HttpStatus.OK);
    }

    @PutMapping("/auth/me")
    public ResponseEntity<UserRestModel> updateUserProfile(Authentication authentication,
            @RequestBody ProfileUpdateDto profileUpdateDto) {
        return new ResponseEntity<UserRestModel>(
                new UserRestModel(userService.updateUserProfile(authentication.getName(), profileUpdateDto)),
                HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserRestModel> getUserProfileWithBlogs(@PathVariable Long id) {

        return new ResponseEntity<UserRestModel>(new UserRestModel(userService.getUserWithProfileAndBlogs(id)),
                HttpStatus.OK);
    }
}
