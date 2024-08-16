package com.example.demo.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostSummaryDTO {
    private Long id;
    private String title;
    private String contents;
    private LocalDateTime createdAt;
    private String userName;
    // likeCount와 commentCount는 별도의 테이블이 있다고 가정하고 제외했습니다.
}