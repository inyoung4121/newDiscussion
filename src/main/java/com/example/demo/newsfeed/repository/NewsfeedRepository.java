package com.example.demo.newsfeed.repository;

import com.example.demo.newsfeed.domain.Newsfeed;
import com.example.demo.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsfeedRepository extends JpaRepository<Newsfeed, Long> {

    // 특정 사용자의 뉴스피드를 최신순으로 가져오기
    Page<Newsfeed> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
