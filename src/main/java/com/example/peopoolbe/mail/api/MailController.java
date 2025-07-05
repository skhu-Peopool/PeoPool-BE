package com.example.peopoolbe.mail.api;

import com.example.peopoolbe.mail.api.dto.CodeCheckDto;
import com.example.peopoolbe.mail.api.dto.MailDto;
import com.example.peopoolbe.mail.service.MailService;
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

    @PostMapping("/codesend")
    public ResponseEntity<String> emailCheck(@RequestBody @Valid MailDto mailDto) throws MessagingException {
        String authCode = mailService.sendCodeMessage(mailDto.email());

        return ResponseEntity.ok(authCode);
    }

    @PostMapping("/mailcodecheck")
    public ResponseEntity<String> checkMail(@RequestBody @Valid CodeCheckDto codeCheckDto) throws MessagingException {
        boolean checked = mailService.checkCode(codeCheckDto.email(), codeCheckDto.authCode());

        if (checked) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.status(403).body("인증 실패, 코드 불일치");
    }
}
