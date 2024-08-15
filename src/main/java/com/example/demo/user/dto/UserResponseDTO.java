package com.example.demo.user.dto;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String profile;
    private String intro;
}
