package com.example.demo.posts.controller;

import com.example.demo.jwt.JwtUtil;
import com.example.demo.posts.dto.PagedPostResponse;
import com.example.demo.posts.dto.PostDTO;
import com.example.demo.posts.dto.PostDetailDTO;
import com.example.demo.posts.exception.PostNotFoundException;
import com.example.demo.posts.service.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
    public ModelAndView getPostById(@PathVariable Long id) {
        log.info("Received request to fetch post with id: {}", id);

        PostDetailDTO post = postsService.getPostById(id);
        ModelAndView modelAndView = new ModelAndView("postdetail");
        modelAndView.addObject("post", post);
        return modelAndView;

    }


    @GetMapping("/postwrite")
    public ModelAndView postwrite() {
        ModelAndView model = new ModelAndView("postwrite");
        model.addObject("postDTO", new PostDTO());
        return model;
    }

    @PostMapping("/postwrite")
    public ResponseEntity<String> postwrite(@RequestBody PostDTO postDTO, @RequestHeader("Authorization") String token) {
        // "Bearer " 제거
        token = token.substring(7);
        Long userId = Long.valueOf(jwtUtil.getEmailFromToken(token));

        // postDTO에 userId 설정
        postDTO.setUserId(userId);

        postsService.savePost(postDTO);
        return ResponseEntity.ok("게시물이 성공적으로 작성되었습니다.");
    }

    @PutMapping("/postupdate")
    public ResponseEntity<String> postupdate(@RequestBody PostDTO postDTO) {
        postsService.updatePost(postDTO);
        return ResponseEntity.ok("게시물이 성공적으로 수정되었습니다.");
    }
}