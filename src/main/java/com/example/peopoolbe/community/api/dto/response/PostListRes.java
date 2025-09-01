package com.example.peopoolbe.community.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListRes(
        long totalCount,
        List<PostInfoRes> postList
) {
}
