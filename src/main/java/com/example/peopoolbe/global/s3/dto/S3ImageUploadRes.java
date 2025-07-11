package com.example.peopoolbe.global.s3.dto;

import lombok.Builder;

@Builder
public record S3ImageUploadRes(
        String fileName,
        String fileUrl
) {
}
