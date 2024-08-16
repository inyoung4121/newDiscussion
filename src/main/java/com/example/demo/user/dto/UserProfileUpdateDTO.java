package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDTO {
    private String username;
    private String email;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
