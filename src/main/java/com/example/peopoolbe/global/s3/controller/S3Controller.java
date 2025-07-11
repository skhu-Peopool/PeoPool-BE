package com.example.peopoolbe.global.s3.controller;

import com.example.peopoolbe.global.s3.dto.S3ImageUploadRes;
import com.example.peopoolbe.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "사진 업로")
    @PostMapping("/upload")
    public ResponseEntity<S3ImageUploadRes> uploadFile(Principal principal, MultipartFile multipartfile){
        return ResponseEntity.ok(s3Service.uploadImage(principal, multipartfile));
    }
}
