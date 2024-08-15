package com.example.demo.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UserDTO {
    private String email;
    private String password;
    private String name;
    private MultipartFile profile;
    private String intro;
}
