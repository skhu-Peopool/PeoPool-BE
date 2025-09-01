package com.example.peopoolbe.community.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListRes(
        long totalCount,
        int totalPages,
        int currentPage,
        List<PostInfoRes> postList
) {
}
