package com.example.demo.comments.service;

import com.example.demo.comments.domain.Comment;
import com.example.demo.comments.dto.CommentDTO;
import com.example.demo.comments.repository.CommentsRepository;
import com.example.demo.newsfeed.service.NewsfeedService;
import com.example.demo.posts.domain.Post;
import com.example.demo.posts.exception.PostNotFoundException;
import com.example.demo.posts.repository.PostsRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.UserNotFoundException;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentsRepository commentsRepository;
    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final NewsfeedService newsfeedService;


    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId) {
        return commentsRepository.findByPostIdOrderByCreatedAtDesc(postId).stream()
                .map(CommentDTO::from)
                .collect(Collectors.toList());
    }


    @Transactional
    public CommentDTO addComment(Long postId, Long userId, String content) {
        Post post = postsRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .contents(content)
                .createdAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentsRepository.save(comment);

        newsfeedService.handleCommentEvent(comment);
        return CommentDTO.from(savedComment);
    }

}
