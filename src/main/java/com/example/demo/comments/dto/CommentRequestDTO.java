package com.example.demo.comments.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    private Long postId;
    private Long userId;
    private String contents;
}