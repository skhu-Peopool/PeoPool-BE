package com.example.peopoolbe.mail.api.dto;

import jakarta.validation.constraints.Email;

public record CodeCheckDto(
        @Email String email,
        Integer authCode
) {
}
