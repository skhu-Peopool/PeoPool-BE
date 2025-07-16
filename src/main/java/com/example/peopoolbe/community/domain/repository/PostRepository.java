package com.example.peopoolbe.community.domain.repository;

import com.example.peopoolbe.community.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByTitleContainingOrContentContainingOrMember_NicknameContainingOrderById(Pageable pageable, String title, String content, String nickname);
}
