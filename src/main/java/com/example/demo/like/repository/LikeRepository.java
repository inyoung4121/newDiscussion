package com.example.demo.like.repository;

import com.example.demo.like.comm.LikeType;
import com.example.demo.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, LikeType targetType);

    void deleteByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, LikeType targetType);

    long countByTargetId(Long postId);
    long countByTargetIdAndTargetType(Long targetId, LikeType targetType);
    Optional<Object> findByTargetIdAndUserId(Long postId, Long userId);
}
