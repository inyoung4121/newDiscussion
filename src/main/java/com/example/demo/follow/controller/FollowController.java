package com.example.demo.follow.controller;

import com.example.demo.follow.service.FollowService;
import com.example.demo.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;
    private final JwtUtil jwtUtil;


    @PostMapping("/{followingId}")
    public ResponseEntity<?> follow(HttpServletRequest request, @PathVariable Long followingId) {
        String token = jwtUtil.extractTokenFromRequest(request);
        Long followerId = Long.valueOf(jwtUtil.getEmailFromToken(token));
        followService.follow(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<?> unfollow(HttpServletRequest request, @PathVariable Long followingId) {
        String token = jwtUtil.extractTokenFromRequest(request);
        Long followerId = Long.valueOf(jwtUtil.getEmailFromToken(token));
        followService.unfollow(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-following/{followingId}")
    public ResponseEntity<Boolean> isFollowing(HttpServletRequest request, @PathVariable Long followingId) {
        String token = jwtUtil.extractTokenFromRequest(request);
        Long followerId = Long.valueOf(jwtUtil.getEmailFromToken(token));
        boolean isFollowing = followService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(isFollowing);
    }
}