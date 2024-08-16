package com.example.demo.user.dto;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class UserProfileDTO {
    private Long id;
    private String email;
    private String name;
    private String profile;  // 프로필 이미지 URL
    private String intro;
    private long followerCount;
    private long followingCount;
}