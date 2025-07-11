package com.example.peopoolbe.global.jwt.api;

import com.example.peopoolbe.global.jwt.api.dto.AccTokenResDto;
import com.example.peopoolbe.global.jwt.service.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenProvider tokenProvider;

    @Operation(summary = "엑세스토큰 요청", description = "쿠키에 담긴 리프레쉬토큰으로 엑세스토큰 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "엑세스토큰 생성")
    })
    @PostMapping("/token")
    public ResponseEntity<AccTokenResDto> accessTokenReIssue(@CookieValue String refreshToken) {
        return ResponseEntity.ok(tokenProvider.accessTokenReIssue(refreshToken));
    }
}
