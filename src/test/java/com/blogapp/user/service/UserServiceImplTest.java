package com.blogapp.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blogapp.exception.AppException;
import com.blogapp.security.jwt.JwtService;
import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.entity.User;
import com.blogapp.user.profile.Profile;
import com.blogapp.user.profile.ProfileUpdateDto;
import com.blogapp.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUsername("test@test.com");
        user.setId(1l);
        user.setPassword("PASSWORD");
    }

    @Test
    void loginUser_validUsernamePasswordReturnToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtService.generateToken(any(Authentication.class))).thenReturn("jwtToken");

        var response = userServiceImpl.login(new AuthRequestDto("validPassword", "test@test.com"));

        assertEquals(response.getToken(), "jwtToken");
        verify(authenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void loginUser_authenticationManagerThrows() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid Credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            userServiceImpl.login(new AuthRequestDto("USERNAME", "PASSWORD"));
        });
    }

    @Test
    void registerUser_usernameAlreadyExistsThrows() {
        when(userRepository.findByUsername("already-taken")).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> {
            userServiceImpl.register(new AuthRequestDto("password", "already-taken"));
        });
    }

    @Test
    void registerUser_validRequest() throws BadRequestException {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        var result = userServiceImpl.register(new AuthRequestDto("password", "username"));

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
        assertEquals("success", result.getMessage());

    }

    @Test
    void findUserWithProfile() {
        User userWithProfile = new User(1L, "USENRAME", "password", new Profile());
        when(userRepository.findUserWithProfile(anyString())).thenReturn(Optional.of(userWithProfile));

        var result = userServiceImpl.findUserWithProfile(userWithProfile.getUsername());
        verify(userRepository).findUserWithProfile("USENRAME");
        assertEquals(userWithProfile.getUsername(), result.getUsername());
    }

    @Test
    void updateUserProfileWhenUserFound() {
        User returnedUser = new User(null, "username", null, new Profile(1L, "fakebio", null, 23));
        when(userRepository.findUserWithProfile("username")).thenReturn(Optional.of(returnedUser));
        when(userRepository.save(returnedUser)).thenReturn(returnedUser);
        ProfileUpdateDto profileUpdateDto = ProfileUpdateDto.builder().age(22).bio("realbio").build();
        var result = userServiceImpl.updateUserProfile("username", profileUpdateDto);

        assertEquals(result.getProfile().getAge(), profileUpdateDto.getAge());
        assertEquals(result.getUsername(), "username");
        assertNull(result.getProfile().getOccupation());

        verify(userRepository).save(returnedUser);
    }

    @Test
    void whenUserNotFoundThrows() {
        when(userRepository.findUserWithProfile(anyString())).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userServiceImpl.updateUserProfile("username", ProfileUpdateDto.builder().build()));
    }
}
