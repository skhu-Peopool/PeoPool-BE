package com.example.peopoolbe.mail.api.dto;

import jakarta.validation.constraints.Email;

public record MailDto(
        @Email String email
) {
}
