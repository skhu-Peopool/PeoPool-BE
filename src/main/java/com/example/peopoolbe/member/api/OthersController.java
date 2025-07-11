package com.example.peopoolbe.member.api;

import com.example.peopoolbe.member.api.dto.response.UserList;
import com.example.peopoolbe.member.service.OthersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OthersController {

    private final OthersService othersService;

    @Operation(summary = "다른 유저 프로필 열람", description = "프로필 공개한 다른 유저의 프로필 리스트를 반환")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리스트 반환"),
            @ApiResponse(responseCode = "403", description = "엑세스토큰 없음")
    })
    @GetMapping("/others")
    public ResponseEntity<UserList> searchOthers() {
        return ResponseEntity.ok(othersService.SearchOthers());
    }
}
