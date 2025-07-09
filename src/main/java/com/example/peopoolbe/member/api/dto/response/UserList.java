package com.example.peopoolbe.member.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record UserList(
        List<UserInfo> userList
) {
    public static UserList fromUserInfo(List<UserInfo> userList) {
        return UserList.builder()
                .userList(userList)
                .build();
    }
}
