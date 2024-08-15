package com.example.demo.follow.controller;

import com.example.demo.follow.service.FollowService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{followerId}/follow/{followingEmail}")
    public void follow(@PathVariable Long followerId, @PathVariable String followingEmail) {
        followService.follow(followerId, followingEmail);
    }

    @DeleteMapping("/{followerId}/unfollow/{followingEmail}")
    public void unfollow(@PathVariable Long followerId, @PathVariable String followingEmail) {
        followService.unfollow(followerId, followingEmail);
    }

    @GetMapping("/{followerId}/is-following/{followingEmail}")
    public boolean isFollowing(@PathVariable Long followerId, @PathVariable String followingEmail) {
        return followService.isFollowing(followerId, followingEmail);
    }
}
