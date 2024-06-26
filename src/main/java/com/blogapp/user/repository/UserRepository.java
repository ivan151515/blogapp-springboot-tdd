package com.blogapp.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.blogapp.user.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("Select u from User u where u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("Select u from User u JOIN FETCH u.profile where u.username = :username")
    Optional<User> findUserWithProfile(String username);

}
