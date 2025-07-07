package com.example.peopoolbe.community.domain.repository;

import com.example.peopoolbe.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByTitleContainingOrderById(String title);

    List<Post> findByContentContainingOrderById(String content);

    List<Post> findByMember_NicknameContainingOrderById(String nickname);
}
