package com.blogapp.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import com.blogapp.security.jwt.JwtServiceImpl;
import com.blogapp.user.entity.User;

@ExtendWith(MockitoExtension.class)

public class JwtServiceImplTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private JwtServiceImpl jwtServiceImpl;

    @Test
    void issuesValidToken() {
        var authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("username");
        when(jwtEncoder.encode(any())).thenReturn(mock(Jwt.class));
        when(authentication.getPrincipal()).thenReturn(new AuthUserDetails(new User(1L, null, null, null, null)));
        jwtServiceImpl.generateToken(authentication);

        verify(jwtEncoder).encode(any());
    }
}
