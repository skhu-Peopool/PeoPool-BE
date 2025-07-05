package com.example.peopoolbe.mail.api;

import com.example.peopoolbe.mail.api.dto.CodeCheckDto;
import com.example.peopoolbe.mail.api.dto.MailDto;
import com.example.peopoolbe.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @Operation(summary = "이메일 인증 코드 전송", description = "이메일로 인증 코드를 전송")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "코드 전송")
    })
    @PostMapping("/codesend")
    public ResponseEntity<String> emailCheck(@RequestBody @Valid MailDto mailDto) throws MessagingException {
        String authCode = mailService.sendCodeMessage(mailDto.email());

        return ResponseEntity.ok(authCode);
    }

    @Operation(summary = "코드 일치 확인", description = "이메일에 전송된 코드의 일치 여부 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 성공"),
            @ApiResponse(responseCode = "403", description = "인증 실패")
    })
    @PostMapping("/mailcodecheck")
    public ResponseEntity<String> checkMail(@RequestBody @Valid CodeCheckDto codeCheckDto) throws MessagingException {
        boolean checked = mailService.checkCode(codeCheckDto.email(), codeCheckDto.authCode());

        if (checked) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.status(403).body("인증 실패, 코드 불일치");
    }
}
