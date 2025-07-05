package com.example.peopoolbe.global.jwt.api;

import com.example.peopoolbe.global.jwt.api.dto.AccTokenResDto;
import com.example.peopoolbe.global.jwt.service.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenProvider tokenProvider;

    @PostMapping("/token")
    public ResponseEntity<AccTokenResDto> accessTokenReIssue(@CookieValue String refreshToken) {
        return ResponseEntity.ok(tokenProvider.accessTokenReIssue(refreshToken));
    }
}
