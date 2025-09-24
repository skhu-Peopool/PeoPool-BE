package com.example.peopoolbe.global.s3.domain.repository;

import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.global.s3.domain.Image;
import com.example.peopoolbe.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    void deleteByMember(Member member);

    List<Image> findAllByPost(Post post);

    void deleteByPath(String path);
}
