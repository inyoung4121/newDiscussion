package com.example.demo.newsfeed.service;

import com.example.demo.comments.domain.Comment;
import com.example.demo.follow.domain.Follow;
import com.example.demo.follow.repository.FollowRepository;
import com.example.demo.like.domain.Like;
import com.example.demo.newsfeed.EventType;
import com.example.demo.newsfeed.domain.Newsfeed;
import com.example.demo.newsfeed.repository.NewsfeedRepository;
import com.example.demo.posts.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NewsfeedService {

    private final NewsfeedRepository newsfeedRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createNewsfeed(EventType eventType, User actor, Long targetId, Long referenceId) {
        List<User> followers = followRepository.findFollowersByFollowing(actor);
        for (User follower : followers) {
            Newsfeed newsfeed = Newsfeed.builder()
                    .user(follower)
                    .actor(actor)
                    .eventType(eventType)
                    .eventReferenceId(referenceId)
                    .createdAt(LocalDateTime.now())
                    .build();

            newsfeedRepository.save(newsfeed);
        }

        if (eventType == EventType.MYPOSTLIKE || eventType == EventType.MYPOSTREPLY) {
            User targetOwner = userRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Newsfeed targetNewsfeed = Newsfeed.builder()
                    .user(targetOwner)
                    .actor(actor)
                    .eventType(eventType)
                    .eventReferenceId(referenceId)
                    .createdAt(LocalDateTime.now())
                    .build();
            newsfeedRepository.save(targetNewsfeed);
        }
    }

    @Transactional
    public void handlePostEvent(Post post) {
        createNewsfeed(EventType.FOLLOWERPOST, post.getUser(), post.getUser().getId(), post.getId());
    }

    @Transactional
    public void handleLikeEvent(Like like) {
        createNewsfeed(EventType.FOLLOWERLIKE, like.getUser(), like.getTargetId(), like.getId());
        createNewsfeed(EventType.MYPOSTLIKE, like.getUser(), like.getTargetId(), like.getTargetId());
    }

    @Transactional
    public void handleFollowEvent(Follow follow) {
        createNewsfeed(EventType.FOLLOWSTART, follow.getFollowing(), follow.getFollower().getId(), follow.getId());
    }

    @Transactional
    public void handleCommentEvent(Comment comment) {
        createNewsfeed(EventType.MYPOSTREPLY, comment.getUser(), comment.getPost().getId(), comment.getId());
        createNewsfeed(EventType.FOLLOWERCOMMENT, comment.getUser(), comment.getUser().getId(), comment.getId());
    }


    @Transactional(readOnly = true)
    public Page<Newsfeed> getLatestNewsfeeds(User user, int page) {
        PageRequest pageable = PageRequest.of(page, 10);
        return newsfeedRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
}
