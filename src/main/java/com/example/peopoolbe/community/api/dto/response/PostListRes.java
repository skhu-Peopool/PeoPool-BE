package com.example.peopoolbe.community.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListRes(
        List<PostInfoRes> postList
) {
    public static PostListRes fromPostList(List<PostInfoRes> postList) {
        return PostListRes.builder()
                .postList(postList)
                .build();
    }
}
