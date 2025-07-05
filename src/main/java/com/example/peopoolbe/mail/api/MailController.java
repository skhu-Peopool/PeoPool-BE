package com.example.peopoolbe.mail.api;

import com.example.peopoolbe.mail.api.dto.MailDto;
import com.example.peopoolbe.mail.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/emailcheck")
    public ResponseEntity<String> emailCheck(@RequestBody MailDto mailDto) throws MessagingException {
        String authCode = mailService.sendSimpleMessage(mailDto.email());

        return ResponseEntity.ok(authCode);
    }
}
