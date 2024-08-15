package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupDTO {
    private String email;
    private String password;
    private String name;
    private String profile;
    private String intro;
}
