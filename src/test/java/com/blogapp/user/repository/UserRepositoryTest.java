package com.blogapp.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.blogapp.user.entity.User;
import com.blogapp.user.profile.Profile;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User saveedUser;

    @BeforeEach
    void setUp() {
        saveedUser = new User();
        saveedUser.setPassword("passwrod");
        saveedUser.setUsername("username");
        var profile = new Profile();
        profile.setAge(22);
        profile.setBio("i am bio");
        saveedUser.setProfile(profile);
        userRepository.save(saveedUser);
    }

    @Test
    void findWithProfileReturnsUserWithProfile() {
        var result = userRepository.findUserWithProfile("username");
        assertNotNull(result.get().getProfile());
        assertEquals(result.get().getProfile().getAge(), saveedUser.getProfile().getAge());
    }
    // @Test
    // void findByUsernameReturnsUserWithOutProfile() {
    // var result = userRepository.findByUsername(null)
    // }
}
