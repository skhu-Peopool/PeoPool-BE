package com.example.peopoolbe.global.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.peopoolbe.global.s3.dto.S3ImageUploadRes;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.repository.MemberRepository;
import com.example.peopoolbe.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3ImageUploadRes uploadImage(Principal principal, MultipartFile multipartFile) {
        if(multipartFile.isEmpty()) {
            return null;
        }
        Member member = memberService.getUserByToken(principal);
        String formerFileName = extractFormerFileName(member);
        System.out.println(formerFileName);

        String fileName = createFileName(multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try(InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3.putObject(bucket, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }

        String url = amazonS3.getUrl(bucket, fileName).toString();
        member.updateImage(url);
        memberRepository.save(member);

        deleteImage(formerFileName);

        return S3ImageUploadRes.builder()
                .fileName(fileName)
                .fileUrl(url)
                .build();
    }

    private String extractFormerFileName(Member member) {
        String formerFileUrl = member.getProfileImage();

        String regex = "([^/]+\\.jpg)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formerFileUrl);

        if(matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ").");
        }
    }

    private void deleteImage(String fileName) {
        if(fileName == null || fileName.isEmpty() || fileName.equals("default.png"))
            return;
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
