package com.example.demo.posts.service;


import com.example.demo.newsfeed.service.NewsfeedService;
import com.example.demo.posts.domain.Post;
import com.example.demo.posts.dto.PostDTO;
import com.example.demo.posts.repository.PostsRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final NewsfeedService newsfeedService;

    @Transactional
    public Post savePost(PostDTO postDTO) {
        User user = userRepository.findById(postDTO.getUserId()).get();
        Post post = Post.builder()
                .title(postDTO.getTitle())
                .contents(postDTO.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        newsfeedService.handlePostEvent(post);
        return (Post) postsRepository.save(post);
    }

    @Transactional
    public Post updatePost(PostDTO updatedPost) {
        Optional<Post> existingPostOptional = postsRepository.findById(updatedPost.getId());
        if (existingPostOptional.isPresent()) {
            Post existingPost = existingPostOptional.get();
            existingPost.updateTitle(updatedPost.getTitle());
            existingPost.updateContents(updatedPost.getContent());
            return (Post) postsRepository.save(existingPost);
        } else {
            throw new RuntimeException("Post not found with id: " + updatedPost.getId());
        }
    }
}
