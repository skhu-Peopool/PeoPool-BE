package com.example.peopoolbe.global.scheduler;

import com.example.peopoolbe.community.post.domain.Post;
import com.example.peopoolbe.community.post.domain.PostStatus;
import com.example.peopoolbe.community.post.domain.repository.PostRepository;
import com.example.peopoolbe.global.jwt.domain.RefreshToken;
import com.example.peopoolbe.global.jwt.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SchedulerService {

    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(zone = "Asia/Seoul", cron = "1 0 0 * * *")
    public void updatePostStatus() {
        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            if(post.getRecruitmentEndDate().isBefore(LocalDate.now()))
                if(post.getPostStatus() != PostStatus.RECRUITED) {
                    post.updateStatus(PostStatus.RECRUITED);
                    postRepository.save(post);
                }

            if(post.getRecruitmentStartDate().isEqual(LocalDate.now()))
                if(post.getPostStatus() != PostStatus.RECRUITING) {
                    post.updateStatus(PostStatus.RECRUITING);
                    postRepository.save(post);
                }
        }
    }

    @Scheduled(zone = "Asia/Seoul", cron = "1 0 0 * * *")
    public void deleteRefreshToken() {
        List<RefreshToken> refreshTokenList = refreshTokenRepository.findAll();
        for (RefreshToken refreshToken : refreshTokenList) {
            if (refreshToken.getCreatedAt().plusWeeks(2).isBefore(LocalDateTime.now())) {
                refreshTokenRepository.delete(refreshToken);
            }
        }
    }
}
