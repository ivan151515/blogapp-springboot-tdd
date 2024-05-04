package com.blogapp.user.dto;

import com.blogapp.user.profile.Profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private Profile profile;

}
