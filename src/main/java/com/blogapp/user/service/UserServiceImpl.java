package com.blogapp.user.service;

import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blogapp.security.jwt.JwtService;
import com.blogapp.user.dto.AuthRequestDto;
import com.blogapp.user.dto.LoginResponseDTO;
import com.blogapp.user.dto.RegisterResponseDTO;
import com.blogapp.user.dto.UserDTO;
import com.blogapp.user.entity.User;
import com.blogapp.user.profile.Profile;
import com.blogapp.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public LoginResponseDTO login(AuthRequestDto loginDto) {

        var auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
                loginDto.getPassword()));

        return new LoginResponseDTO(jwtService.generateToken(auth));
    }

    @Override
    public RegisterResponseDTO register(@Valid AuthRequestDto authRequestDto) throws BadRequestException {
        Optional<User> userAlreadyExists = userRepository.findByUsername(authRequestDto.getUsername());

        if (userAlreadyExists.isPresent()) {
            throw new BadRequestException("Bad credentials");
        }

        User user = new User();
        user.setPassword(passwordEncoder.encode(authRequestDto.getPassword()));
        user.setUsername(authRequestDto.getUsername());
        var profile = new Profile();
        user.setProfile(profile);
        userRepository.save(user);
        return new RegisterResponseDTO("success");
        // return new RegisterResponseDTO("User registered succesfully");
    }

    @Override
    public UserDTO findUserWithProfile(String username) {
        var user = userRepository.findUserWithProfile(username)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        return new UserDTO(user.getUsername(), user.getProfile());
    }

}
