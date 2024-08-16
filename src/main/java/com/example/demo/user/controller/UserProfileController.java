package com.example.demo.user.controller;
import com.example.demo.follow.service.FollowService;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.posts.service.PostsService;
import com.example.demo.user.dto.PostSummaryDTO;
import com.example.demo.user.dto.UserProfileDTO;
import com.example.demo.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    private final UserService userService;
    private final PostsService postsService;
    private final FollowService followService;
    private final JwtUtil jwtUtil;

    public UserProfileController(UserService userService, PostsService postsService,
                                 FollowService followService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.postsService = postsService;
        this.followService = followService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/user/{userId}")
    public ModelAndView getUserProfile(@PathVariable Long userId, HttpServletRequest request) {
        log.info("Received request to fetch user profile with id: {}", userId);

        ModelAndView modelAndView = new ModelAndView("userProfile");

        try {
            UserProfileDTO userProfile = userService.getUserProfile(userId);
            modelAndView.addObject("userDTO", userProfile);

            Long currentUserId = getUserIdFromJwtToken(request);

            boolean isSelf = false;
            boolean isFollowing = false;

            if (currentUserId != null) {
                isSelf = currentUserId.equals(userId);
                if (!isSelf) {
                    isFollowing = followService.isFollowing(currentUserId, userId);
                }
            }

            modelAndView.addObject("isSelf", isSelf);
            modelAndView.addObject("isFollowing", isFollowing);
            modelAndView.addObject("currentUserId", currentUserId);

            // 사용자의 최근 게시물 목록 가져오기
            List<PostSummaryDTO> recentPosts = postsService.getRecentPostsByUser(userId, 5); // 최근 5개 게시물
            modelAndView.addObject("recentPosts", recentPosts);

        } catch (Exception e) {
            log.error("Error fetching user profile with id: {}", userId, e);
            modelAndView.setViewName("error");
            modelAndView.addObject("errorMessage", "사용자 프로필을 불러오는 데 실패했습니다.");
        }

        return modelAndView;
    }

    private Long getUserIdFromJwtToken(HttpServletRequest request) {
        String token = jwtUtil.extractTokenFromRequest(request);
        if (token != null) {
            try {
                return Long.valueOf(jwtUtil.getEmailFromToken(token));
            } catch (Exception e) {
                log.error("Error extracting user ID from token", e);
            }
        }
        return null;
    }

}