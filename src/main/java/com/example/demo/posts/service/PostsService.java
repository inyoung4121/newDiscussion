package com.example.demo.posts.service;


import com.example.demo.newsfeed.service.NewsfeedService;
import com.example.demo.posts.domain.Post;
import com.example.demo.posts.dto.PagedPostResponse;
import com.example.demo.posts.dto.PostDTO;
import com.example.demo.posts.dto.PostDetailDTO;
import com.example.demo.posts.dto.ResponsePostDTO;
import com.example.demo.posts.exception.PostNotFoundException;
import com.example.demo.posts.repository.PostsRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
                .contents(postDTO.getContents())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .user(user)
                .build();

        newsfeedService.handlePostEvent(post);
        return postsRepository.save(post);
    }

    @Transactional
    public Post updatePost(PostDTO updatedPost) {
        Optional<Post> existingPostOptional = postsRepository.findById(updatedPost.getId());
        if (existingPostOptional.isPresent()) {
            Post existingPost = existingPostOptional.get();
            existingPost.updateTitle(updatedPost.getTitle());
            existingPost.updateContents(updatedPost.getContents());
            return postsRepository.save(existingPost);
        } else {
            throw new RuntimeException("Post not found with id: " + updatedPost.getId());
        }
    }

    @Transactional(readOnly = true)
    public PostDetailDTO getPostById(Long id) {
        log.debug("Fetching post with id: {}", id);

        return postsRepository.findById(id)
                .map(PostDetailDTO::from)
                .orElseThrow(() -> {
                    log.warn("Attempted to fetch non-existent post with id: {}", id);
                    return new PostNotFoundException(id);
                });
    }

    public PagedPostResponse getRecentPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postsRepository.findAllByOrderByCreatedAtDesc(pageable);

        Page<ResponsePostDTO> postDTOs = posts.map(this::convertToDTO);
        return new PagedPostResponse(postDTOs);
    }


    private ResponsePostDTO convertToDTO(Post post) {
        return ResponsePostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .userName(post.getUser().getName())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
