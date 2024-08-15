package com.example.demo.posts.controller;

import com.example.demo.jwt.JwtUtil;
import com.example.demo.posts.dto.PagedPostResponse;
import com.example.demo.posts.dto.PostDTO;
import com.example.demo.posts.dto.PostDetailDTO;
import com.example.demo.posts.exception.PostNotFoundException;
import com.example.demo.posts.service.PostsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostsController {

    private final PostsService postsService;
    private final JwtUtil jwtUtil;


    @GetMapping("/api/posts/recent")
    public PagedPostResponse getRecentPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return postsService.getRecentPosts(page, size);
    }


    @GetMapping("/post/{id}")
    public ModelAndView getPostById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Received request to fetch post with id: {}", id);

        PostDetailDTO post = postsService.getPostById(id);

        Long curUserId = getUserIdFromJwtToken(request);

        boolean isAuthor = Objects.equals(curUserId, post.getUserId());

        ModelAndView modelAndView = new ModelAndView("postdetail");
        modelAndView.addObject("isAuthor", isAuthor);
        modelAndView.addObject("post", post);
        return modelAndView;

    }


    @GetMapping("/postwrite")
    public ModelAndView postwrite(@RequestParam(required = false) Long id) {
        ModelAndView model = new ModelAndView("postwrite");
        if (id != null) {
            // 수정 모드: 기존 게시물 정보를 가져옴
            PostDetailDTO existingPost = postsService.getPostById(id);
            PostDTO postDTO = new PostDTO();
            postDTO.setId(existingPost.getId());
            postDTO.setTitle(existingPost.getTitle());
            postDTO.setContents(existingPost.getContents());
            model.addObject("postDTO", postDTO);
            model.addObject("isEdit", true);
        } else {
            // 새 글 작성 모드
            model.addObject("postDTO", new PostDTO());
            model.addObject("isEdit", false);
        }
        return model;
    }

    @PostMapping("/postwrite")
    public ResponseEntity<String> postwrite(@RequestBody PostDTO postDTO, @RequestHeader("Authorization") String token) {
        // "Bearer " 제거
        token = token.substring(7);
        Long userId = Long.valueOf(jwtUtil.getEmailFromToken(token));

        // postDTO에 userId 설정
        postDTO.setUserId(userId);

        if (postDTO.getId() != null) {
            // 수정 모드
            postsService.updatePost(postDTO);
            return ResponseEntity.ok("게시물이 성공적으로 수정되었습니다.");
        } else {
            // 새 글 작성 모드
            postsService.savePost(postDTO);
            return ResponseEntity.ok("게시물이 성공적으로 작성되었습니다.");
        }
    }

    @PutMapping("/postupdate")
    public ResponseEntity<String> postupdate(@RequestBody PostDTO postDTO) {
        postsService.updatePost(postDTO);
        return ResponseEntity.ok("게시물이 성공적으로 수정되었습니다.");
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