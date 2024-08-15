package com.example.demo.posts.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PostDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;

    @Builder
    public PostDTO(Long id ,Long userId, String title, String content) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }
}
