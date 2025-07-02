package com.example.peopoolbe.global.jwt.domain.repository;

import com.example.peopoolbe.global.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
