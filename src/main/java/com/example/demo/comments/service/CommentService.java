package com.example.demo.comments.service;

import com.example.demo.comments.domain.Comment;
import com.example.demo.comments.repository.CommentsRepository;
import com.example.demo.newsfeed.service.NewsfeedService;
import com.example.demo.posts.domain.Post;
import com.example.demo.posts.repository.PostsRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final NewsfeedService newsfeedService;

    @Transactional
    public Comment addComment(Long postId, Long userId, String contents) {
        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .contents(contents)
                .createdAt(LocalDateTime.now())
                .build();

        newsfeedService.handleCommentEvent(comment);

        return commentsRepository.save(comment);
    }
}
