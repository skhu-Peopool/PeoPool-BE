package com.example.peopoolbe.community.domain.repository;

import com.example.peopoolbe.community.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN p.member m " +
            "WHERE ( (p.title LIKE :query OR p.content LIKE :query OR m.nickname LIKE :query) " +
            "AND p.recruitmentEndDate BETWEEN :start AND :end )")
    Page<Post> searchPost(Pageable pageable, @Param("query") String query, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
