package com.example.demo.comments.domain;
import com.example.demo.comments.dto.CommentRequestDTO;
import com.example.demo.posts.domain.Post;
import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 필드 업데이트 메서드
    public void updateContents(String contents) {
        this.contents = contents;
    }

    // 정적 메서드로 DTO를 엔티티로 변환 (예시)
    public static Comment fromDto(CommentRequestDTO dto, Post post, User user) {
        return Comment.builder()
                .post(post)
                .user(user)
                .contents(dto.getContents())
                .createdAt(LocalDateTime.now())
                .build();
    }
}