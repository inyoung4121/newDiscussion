package com.example.demo.like.service;

import com.example.demo.like.domain.Like;
import com.example.demo.like.repository.LikeRepository;
import com.example.demo.like.comm.LikeType;
import com.example.demo.newsfeed.service.NewsfeedService;
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
public class LikeService {


    private final LikeRepository likeRepository;
    private final PostsRepository postRepository;
    private final UserRepository userRepository;
    private final NewsfeedService newsfeedService;

    @Transactional
    public boolean toggleLike(Long userId, Long targetId, LikeType targetType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        boolean alreadyLiked = likeRepository.existsByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);

        if (alreadyLiked) {
            // 이미 좋아요를 누른 경우, 좋아요 취소
            likeRepository.deleteByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
            return false; // 좋아요를 취소했음을 나타내기 위해 null 반환
        } else {
            // 새로운 좋아요 추가
            Like like = Like.builder()
                    .user(user)
                    .targetId(targetId)
                    .targetType(targetType)
                    .createdAt(LocalDateTime.now())
                    .build();

            newsfeedService.handleLikeEvent(like);
            likeRepository.save(like);
            return true;
        }
    }

    public long getLikeCount(Long postId) {
        return likeRepository.countByTargetId(postId);
    }

    public boolean hasUserLiked(Long postId, Long userId) {
        return likeRepository.findByTargetIdAndUserId(postId, userId).isPresent();
    }

}
