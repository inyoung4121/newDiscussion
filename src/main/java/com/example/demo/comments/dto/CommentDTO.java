package com.example.demo.comments.dto;


import com.example.demo.comments.domain.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private String userName;
    private LocalDateTime createdAt;

    public static CommentDTO from(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContents())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}