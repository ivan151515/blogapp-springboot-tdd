package com.blogapp.security.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.blogapp.security.AuthUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;

    public String generateToken(Authentication authentication) {
        var userDetails = (AuthUserDetails) authentication.getPrincipal();
        var now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claim("id", userDetails.getId())
                .subject(authentication.getName())
                .build();
        var b = JwtEncoderParameters.from(jwtClaimsSet);
        var a = jwtEncoder.encode(b);
        return a.getTokenValue();
    }

}
