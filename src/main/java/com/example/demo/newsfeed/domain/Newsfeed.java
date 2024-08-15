package com.example.demo.newsfeed.domain;

import com.example.demo.newsfeed.EventType;
import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
@Table(name = "newsfeed")
public class Newsfeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 뉴스피드를 받을 사용자

    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor; // 행동을 한 사용자

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(nullable = false)
    private Long eventReferenceId;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
