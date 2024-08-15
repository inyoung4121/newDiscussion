package com.example.demo.comments.controller;

import com.example.demo.comments.domain.Comment;
import com.example.demo.comments.dto.CommentRequestDTO;
import com.example.demo.comments.dto.CommentResponseDTO;
import com.example.demo.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@Controller
public class CommentsController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO commentRequest) {
        Comment comment = commentService.addComment(
                commentRequest.getPostId(),
                commentRequest.getUserId(),
                commentRequest.getContents()
        );

        CommentResponseDTO response = CommentResponseDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .contents(comment.getContents())
                .createdAt(comment.getCreatedAt())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
