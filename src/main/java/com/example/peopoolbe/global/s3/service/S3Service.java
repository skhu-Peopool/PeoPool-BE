package com.example.peopoolbe.global.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.community.post.domain.repository.PostRepository;
import com.example.peopoolbe.global.s3.domain.Image;
import com.example.peopoolbe.global.s3.domain.ImageType;
import com.example.peopoolbe.global.s3.domain.repository.ImageRepository;
import com.example.peopoolbe.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

//    public S3ImageUploadRes uploadProfileImage(Principal principal, MultipartFile multipartFile) {
//        if(multipartFile == null || multipartFile.isEmpty()) {
//            return null;
//        }
//        Member member = memberService.getUserByToken(principal);
//        String formerFileName = extractFormerFileNameFromMember(member);
//        System.out.println(formerFileName);
//
//        String fileName = createFileName(multipartFile.getOriginalFilename());
//
//        awsImageUpload(multipartFile, fileName);
//
//        String url = amazonS3.getUrl(bucket, fileName).toString();
////        member.updateImage(url);
////        memberRepository.save(member);
//
//        deleteImage(formerFileName);
//
//        return S3ImageUploadRes.builder()
//                .fileName(fileName)
//                .fileUrl(url)
//                .build();
//    }

    @Transactional
    public Image uploadProfileImage(MultipartFile multipartFile, Member member) {
        if(multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        imageRepository.deleteByMember(member);

        String fileName = member.getNickname() + "_profile";
        awsImageUpload(multipartFile, fileName);

        Image image = Image.builder()
                .path(amazonS3.getUrl(bucket, fileName).toString())
                .fileName(fileName)
                .imageType(ImageType.PROFILE)
                .member(member)
                .build();
        imageRepository.save(image);

        return image;
    }

    @Transactional
    public Image uploadPostImage(MultipartFile multipartFile, Post post) {
        if(multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        imageRepository.deleteAllByPost(post);

//        Image[] images = new Image[multipartFiles.length];
//        for(MultipartFile multipartFile : multipartFiles) {
//            String prefix = "Post_" + postId + "_";
//            String fileName = prefix + multipartFile.getOriginalFilename();
//            awsImageUpload(multipartFile, fileName);
//
//            images[multipartFile.get]
//        }
//        IntStream.range(0, multipartFile.length)
//                .forEach(i -> {
//                    String prefix = "Post_" + post.getId() + "_" + i + "_";
//                    String fileName = prefix + multipartFiles[i].getOriginalFilename();
//                    awsImageUpload(multipartFiles[i], fileName);
//
//                    Image image = Image.builder()
//                            .path(amazonS3.getUrl(bucket, fileName).toString())
//                            .fileName(multipartFiles[i].getOriginalFilename())
//                            .imageType(ImageType.POST)
//                            .post(post)
//                            .build();
//                    imageRepository.save(image);
//                });
//        return image;
        String prefix = "Post_" + post.getId() + "_";
        String fileName = prefix + multipartFile.getOriginalFilename();
        awsImageUpload(multipartFile, fileName);

        return Image.builder()
                .path(amazonS3.getUrl(bucket, fileName).toString())
                .fileName(multipartFile.getOriginalFilename())
                .imageType(ImageType.POST)
                .post(post)
                .build();
    }


//    public String uploadProfileImage(MultipartFile multipartFile, Member member) {
//        if(multipartFile == null || multipartFile.isEmpty()) {
//            return member.getProfileImage();
//        }
//
//        String formerFileName = extractFormerFileNameFromMember(member);
//        System.out.println(formerFileName);
//
//        String fileName = createFileName(multipartFile.getOriginalFilename());
//        awsImageUpload(multipartFile, fileName);
//
//        deleteImage(formerFileName);
//
//        return amazonS3.getUrl(bucket, fileName).toString();
//    }

//    public String uploadNewPostImage(MultipartFile multipartFile) {
//        if(multipartFile == null || multipartFile.isEmpty()) {
//            return null;
//        }
//
//        String fileName = createFileName(multipartFile.getOriginalFilename());
//        awsImageUpload(multipartFile, fileName);
//        return amazonS3.getUrl(bucket, fileName).toString();
//    }
//
//    public String uploadExistingPostImage(MultipartFile multipartFile, Long postId) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        if(multipartFile == null || multipartFile.isEmpty()) {
////            return null;
//            return post.getImage();
//        }
//
//        String formerFileName = extractFormerFileNameFromPost(post);
//
//        String fileName = createFileName(multipartFile.getOriginalFilename());
//        awsImageUpload(multipartFile, fileName);
//
//        deleteImage(formerFileName);
//
//        return amazonS3.getUrl(bucket, fileName).toString();
//    }

    private void awsImageUpload(MultipartFile multipartFile, String fileName) {
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
    }

//    private String extractFormerFileNameFromMember(Member member) {
//        String formerFileUrl = member.getProfileImage();
//
//        String regex = "([^/]+\\.jpg)$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(formerFileUrl);
//
//        if(matcher.find()) {
//            return matcher.group(1);
//        }
//        return null;
//    }

//    private String extractFormerFileNameFromPost(Post post) {
//        String formerFileUrl = post.getImage();
//        if(formerFileUrl != null) {
//            String regex = "([^/]+\\.jpg)$";
//            Pattern pattern = Pattern.compile(regex);
//            Matcher matcher = pattern.matcher(formerFileUrl);
//            if(matcher.find()) {
//                return matcher.group(1);
//            }
//        }
//        return null;
//    }

//    private String createFileName(String fileName) {
//        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
//    }

//    private String getFileExtension(String fileName) {
//        try {
//            return fileName.substring(fileName.lastIndexOf("."));
//        } catch (StringIndexOutOfBoundsException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ").");
//        }
//    }

//    private void deleteImage(String fileName) {
//        if(fileName == null || fileName.isEmpty() || fileName.equals("default.png"))
//            return;
//        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
//    }
}
