package com.example.demo.like.controller;


import com.example.demo.jwt.JwtUtil;
import com.example.demo.like.comm.LikeType;
import com.example.demo.like.domain.Like;
import com.example.demo.like.service.LikeService;
import com.example.demo.posts.service.PostsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class LikeController {
    private final LikeService likeService;
    private final JwtUtil jwtUtil;
    private final PostsService postsService;

    @GetMapping("/api/likes/post/{postId}")
    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = getUserIdFromJwtToken(request);
        boolean hasLiked = likeService.hasUserLiked(postId, userId);
        long likeCount = likeService.getLikeCount(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", hasLiked);
        response.put("likeCount", likeCount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/likes/post/{postId}")
    public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = getUserIdFromJwtToken(request);
        boolean isLinked = likeService.toggleLike(userId,postId,LikeType.POST);
        long likeCount = likeService.getLikeCount(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", isLinked);
        response.put("likeCount", likeCount);
        return ResponseEntity.ok(response);
    }

    public Long getUserIdFromJwtToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("stockJwtToken".equals(cookie.getName())) {
                    try {
                        return Long.valueOf(jwtUtil.getEmailFromToken(cookie.getValue()));
                    } catch (Exception e) {
                        log.error("Invalid token", e);
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
