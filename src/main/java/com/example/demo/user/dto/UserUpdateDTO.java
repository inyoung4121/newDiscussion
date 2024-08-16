package com.example.demo.user.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {
    private String name;
    private String password;
    private String intro;
}