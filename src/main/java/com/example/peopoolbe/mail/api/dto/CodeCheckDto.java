package com.example.peopoolbe.mail.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record CodeCheckDto(
        @Email @NotEmpty String email,
        @NotEmpty String authCode
) {
}
