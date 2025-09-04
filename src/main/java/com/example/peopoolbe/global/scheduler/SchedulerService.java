package com.example.peopoolbe.global.scheduler;

import com.example.peopoolbe.community.domain.Post;
import com.example.peopoolbe.community.domain.Status;
import com.example.peopoolbe.community.domain.repository.PostRepository;
import com.example.peopoolbe.community.service.PostService;
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

    @Scheduled(zone = "Asia/Seoul", cron = "1 0 0 * * *")
    public void updatePostStatus() {
        List<Post> postList = postRepository.findAll();
        for (Post post : postList) {
            if(post.getRecruitmentEndDate().isBefore(LocalDate.now()))
                if(post.getStatus() != Status.RECRUITED) {
                    post.updateStatus(Status.RECRUITED);
                    postRepository.save(post);
                }

            if(post.getRecruitmentStartDate().isEqual(LocalDate.now()))
                if(post.getStatus() != Status.RECRUITING) {
                    post.updateStatus(Status.RECRUITING);
                    postRepository.save(post);
                }
        }
    }
}
