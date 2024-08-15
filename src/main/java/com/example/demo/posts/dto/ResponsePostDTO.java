package com.example.demo.posts.dto;

import com.example.demo.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Setter
@Getter
public class ResponsePostDTO {
    private Long id;
    private String userName;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
}
