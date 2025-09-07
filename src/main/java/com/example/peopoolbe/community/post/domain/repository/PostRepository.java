package com.example.peopoolbe.community.post.domain.repository;

import com.example.peopoolbe.community.post.domain.Category;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.community.post.domain.PostStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN p.member m " +
            "WHERE ( (:query IS NULL OR :query = '' " +
            "OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))" +
//            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(m.nickname) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.recruitmentEndDate BETWEEN :start AND :end " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:status IS NULL OR p.postStatus = :status) ) ",
    countQuery = "SELECT COUNT(DISTINCT p) FROM Post p " +
            "LEFT JOIN p.member m " +
            "WHERE ( (:query IS NULL OR :query = '' " +
            "OR LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))" +
//            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(m.nickname) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.recruitmentEndDate BETWEEN :start AND :end " +
            "AND (:category IS NULL OR p.category = :category) " +
            "AND (:status IS NULL OR p.postStatus = :status) ) ")
    Page<Post> searchPost(Pageable pageable, @Param("query") String query,
                                @Param("start") LocalDate start, @Param("end") LocalDate end,
                                @Param("category") Category category, @Param("status") PostStatus postStatus);

//    @Query("SELECT p FROM Post p LEFT JOIN p.member m " +
//            "WHERE (p.recruitmentEndDate BETWEEN :start AND :end) " +
//            "AND (:category IS NULL OR p.category = :category) " +
//            "AND (:status IS NULL OR p.status = :status) ")
//    Page<Post> searchPost(Pageable pageable, @Param("start") LocalDate start,
//                          @Param("end") LocalDate end, @Param("category") Category category,
//                          @Param("status") Status status);
}
