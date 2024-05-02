package com.blogapp.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

    public String generateToken(Authentication any) {
        // TODO Auto-generated method stub
        return "token";
    }

}
