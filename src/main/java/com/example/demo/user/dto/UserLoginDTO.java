package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginDTO {
    Long id;
    String email;
    String password;
}
