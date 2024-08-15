package com.example.demo.comments.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
public class LikeCommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private String userName;
}