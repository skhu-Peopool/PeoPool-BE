package com.example.peopoolbe.member.api;

import com.example.peopoolbe.member.api.dto.response.UserList;
import com.example.peopoolbe.member.service.OthersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OthersController {

    private final OthersService othersService;

    @GetMapping("/others")
    public ResponseEntity<UserList> searchOthers() {
        return ResponseEntity.ok(othersService.SearchOthers());
    }
}
