package com.example.peopoolbe.community.domain.repository;

import com.example.peopoolbe.community.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
